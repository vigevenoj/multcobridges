package com.sharkbaitextraordinaire.multcobridges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sharkbaitextraordinaire.multcobridges.BridgeUpdate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BridgeUpdateTest {

	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Test
	public void testDeserializateBridgeUpdateFeature() throws Exception {
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
		URL url = getClass().getResource("/bridgedata.json");
		File bridgeData = new File(url.getFile());
		HashMap<String,Object> o = mapper.readValue(bridgeData, typeRef);

		assertNotNull(o.get("changed"));
		assertTrue(o.get("changed") instanceof HashMap<?,?>);
		assertNotNull(o.get("burnside"));
		assertTrue(o.get("burnside") instanceof HashMap<?,?>);
	}

	@Test
	public void testMapper() throws Exception {
		URL url = getClass().getResource("/bridgedata.json");
		File bridgeData = new File(url.getFile());
		BridgeUpdate bu = mapper.readValue(bridgeData, BridgeUpdate.class);
		assertEquals(bu.getChangedBridge(), BridgeUpdate.HAWTHORNE);
		assertEquals(bu.getChangedItem(), "status");

		assertNotNull(bu.getBridgeUpdates());
		assertNotNull(bu.getHawthorne());
		assertEquals(bu.getBridgeUpdates().size(), 4);

	}

	@Test
	public void testManualMapper() throws Exception {
		URL url = getClass().getResource("/bridgedata.json");
		File bridgeData = new File(url.getFile());
		JsonNode root = mapper.readValue(bridgeData, JsonNode.class);
		assertTrue(root instanceof ObjectNode);

		int lastLiftBridgeId = root.get("hawthorne").get("lastFive").get(0).get("bridgeId").asInt();
		assertNotNull(lastLiftBridgeId);

	}

	@Test
	public void testAsMap() throws Exception {
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
		URL url = getClass().getResource("/bridgedata.json");
		File bridgeData = new File(url.getFile());
		HashMap<String,Object> o = mapper.readValue(bridgeData, typeRef);

		String changedBridge = ((HashMap<String,String>) o.get("changed")).get("bridge"); // should be "hawthorne"
		String changedStatus = ((HashMap<String,String>) o.get("changed")).get("item"); // should be "status"

		assertEquals(changedBridge, "hawthorne");
		assertEquals(changedStatus, "status");

		Boolean bridgeStatus = ((HashMap<String,Boolean>) o.get(changedBridge)).get("status");
		assertEquals(bridgeStatus, false); // sample json indicates that bridge is NOT lifted

		ArrayList<HashMap<String,String>> lastFive = ((HashMap<String,ArrayList<HashMap<String,String>>>) o.get(changedBridge)).get("lastFive");
		assertNotNull(lastFive);

		HashMap<String,String> lastEvent = lastFive.get(0);
		assertNotNull(lastEvent);

		String dt = lastEvent.get("downTime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			Date downTime = sdf.parse(dt);
			assertNotNull(downTime);
		} catch (ParseException e) {
		}
	}
}

