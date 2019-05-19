package com.messaging.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

@Path("/")
public class MessageService {
	private UsersDao dao = UsersDao.getInstance();
	private static final String MESSAGE = "message:";
	private static final String ERROR = "error:";

	@POST
	@Path("inbound/sms/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response inboundService(RequestBody request) throws JSONException {
		String returnMessage = "inboud sms ok";
		String error = "";
		String inputValidation = dao.validateAndProcessIncomingSms(request.from, request.to, request.text);
		if (!inputValidation.isEmpty()) {
			returnMessage = "";
			error = inputValidation;
		}
		// returnMessage = returnMessage.concat(request.from).concat(request.to).concat(request.text);
		JSONObject json = new JSONObject();
		json.put(MESSAGE, returnMessage);
		json.put(ERROR, error);
		// String result = MESSAGE.concat(returnMessage).concat(",").concat(ERROR).concat(error);
		String result = json.toString();
		Response response = Response.status(Response.Status.OK).entity(result).build();
		return response;

	}

	@POST
	@Path("outbound/sms/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response outboundService(RequestBody request) throws JSONException {
		String returnMessage = "outbound sms ok";
		String error = "";
		String outputValidation = dao.validataAndProcessOutgoingSms(request.from, request.to, request.text);
		if (!outputValidation.isEmpty()) {
			returnMessage = "";
			error = outputValidation;
		}

		JSONObject json = new JSONObject();
		json.put(MESSAGE, returnMessage);
		json.put(ERROR, error);

		// String result = MESSAGE.concat(returnMessage).concat(",").concat(ERROR).concat(error);
		String result = json.toString();
		Response response = Response.status(Response.Status.OK).entity(result).build();
		return response;

	}
}
