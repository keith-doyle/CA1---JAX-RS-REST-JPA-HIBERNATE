package com.example.greenhouse.rest;

import com.example.greenhouse.dao.UserDAO;
import com.example.greenhouse.model.User;
import com.example.greenhouse.parser.JSONEmissionImporter;
import com.example.greenhouse.parser.XMLEmissionImporter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//REST endpoint to trigger JSON and XML imports from the web app
//Only ADMIN users can call this (checked via X-User-Id)
@Path("/parsers")
@Produces(MediaType.APPLICATION_JSON)
public class ParserRest {

	private final UserDAO userDAO = new UserDAO();

	// Find user by id for X-User-Id header
	private User findUser(Integer id) {
		if (id == null)
			return null;
		return userDAO.findById(id);
	}

	// 401 response helper
	private Response unauthorized() {
		return Response.status(Response.Status.UNAUTHORIZED)
				.entity("{\"error\":\"You must be logged in (X-User-Id header)\"}").build();
	}

	// 403 response helper
	private Response forbidden() {
		return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"Admin role required\"}").build();
	}

	// POST /parsers/run
	// Runs both JSON and XML importers and returns how many rows were inserted
	@POST
	@Path("/run")
	public Response runParsers(@HeaderParam("X-User-Id") Integer userId) {
		User u = findUser(userId);
		if (u == null) {
			return unauthorized();
		}
		// Only ADMINs are allowed to bulk-import data
		if (!"ADMIN".equalsIgnoreCase(u.getRole())) {
			return forbidden();
		}

		try {
			JSONEmissionImporter jsonImporter = new JSONEmissionImporter();
			int jsonCount = jsonImporter.importFromClasspathJson("GreenhouseGasEmissions2025.json");

			XMLEmissionImporter xmlImporter = new XMLEmissionImporter();
			int xmlCount = xmlImporter.importXmlEmissions();

			int total = jsonCount + xmlCount;

			// Simple JSON string summarising the import result
			String resultJson = String.format("{\"jsonInserted\":%d,\"xmlInserted\":%d,\"totalInserted\":%d}",
					jsonCount, xmlCount, total);

			return Response.ok(resultJson).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity("{\"error\":\"Parser error: " + e.getMessage() + "\"}").build();
		}
	}
}
