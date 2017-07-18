package com.webservices.steps;

import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;
import static com.qmetry.qaf.automation.step.CommonStep.requestForResource;
import static com.qmetry.qaf.automation.step.CommonStep.response_should_have_status;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.json.JSONObject;

import com.qmetry.qaf.automation.step.QAFTestStep;
import com.qmetry.qaf.automation.util.Validator;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;
import com.qmetry.qaf.automation.ws.rest.RestWSTestCase;
import com.sun.jersey.api.client.ClientResponse.Status;

public class OrdersSteps extends RestWSTestCase {

	static OrdersSteps os = new OrdersSteps();
	static Map<String, String> clientData=new HashMap<String, String>();

	@QAFTestStep(description = "User post new order {0} with amount {1}")
	public void userPostNewOrderWithAmount(String clientName, String amount) {

		String data = String.format("{'clientName':'%s','amount':'%s'}", clientName, amount);
		getClient().resource(getBundle().getString("env.baseurl") + "/orders.json").post(data);
		clientData.put("clientName", clientName);
		clientData.put("amount", amount);
		verifyResponse(Status.CREATED.getStatusCode());
		int status= getResponse().getStatus().getStatusCode();
		response_should_have_status("Created");

	}

	@QAFTestStep(description = "Order is created with id {0}")
	public void orderIsCreatedWithId(String id) {
		String location = new RestTestBase().getResponse().getHeaders().getFirst("location");
		assertThat("Order location", location, Matchers.notNullValue());
//		System.out.println(location.substring(location.lastIndexOf("/") + 1, location.lastIndexOf(".")));
		getBundle().setProperty(id, location.substring(location.lastIndexOf("/") + 1, location.lastIndexOf(".")));
		clientData.put("id", location.substring(location.lastIndexOf("/") + 1, location.lastIndexOf(".")));
	}
	
	@QAFTestStep(description = "Store client details {0}")
	public void storeClientDetails(String name) {
		getBundle().setProperty(name, clientData);
		System.out.println("stored data");
	}
	

	@QAFTestStep(description = "Order with id {0} is present")
	public void orderWithIdIsPresent(String id) {
		String resource = String.format("/orders/%s.json", id);
		requestForResource(resource);
		response_should_have_status("OK");

	}

	@QAFTestStep(description = "User deletes the order {0}")
	public void userDeletesTheOrder(String id) { 
		String resource = String.format("/orders/%s.json", id);
		getClient().resource(getBundle().getString("env.baseurl") + resource).delete();
	}

	@QAFTestStep(description = "Get all the order details")
	public void getAllTheOrderDetails() {
		String resource = "/posts";
		requestForResource(resource);
		response_should_have_status("OK");
	}

	public static boolean verifyResponse(int statusCode) {
		int response=os.getResponse().getStatus().getStatusCode();
		JSONObject actResponse = new JSONObject(response);
		return Validator.verifyThat("Response Status", os.getResponse().getStatus().getStatusCode(),
				Matchers.equalTo(statusCode));
	}

}
