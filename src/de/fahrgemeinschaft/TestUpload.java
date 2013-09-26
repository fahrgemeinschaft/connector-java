/**
 * Fahrgemeinschaft / Ridesharing App
 * Copyright (c) 2013 by it's authors.
 * Some rights reserved. See LICENSE..
 *
 */

package de.fahrgemeinschaft;

import java.util.Date;

import org.json.JSONObject;
import org.teleportr.Ride;
import org.teleportr.Ride.Mode;

public class TestUpload extends Tests {


    private Ride offer;
    private long later;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        con.login("blabla");
        later = System.currentTimeMillis() + 300*24*3600000;
        offer = new Ride().type(Ride.OFFER).mode(Mode.CAR).activate()
            .dep(new Date(later + 10 * 3600)).price(0)
            .from(stuttgart).via(munich).via(leipzig).to(berlin)
            .set("Email", "foo@bar.baz")
            .set("Mobile", "01234567")
            .set("Landline", "001234")
            .set("NumberPlate", "MX-123C")
            .set("Comment", "Hi there..");
        offer.getDetails().put("Privacy", new JSONObject(
             "{ \"Name\": \"5\","       // nobody
             + "\"Landline\": \"4\","   // members
             + "\"Email\": \"0\","      // request
             + "\"Mobile\": \"1\","     // anybody
             + "\"NumberPlate\": \"1\" } "));
//            offer.getDetails().put("Reoccur", new JSONObject(
//                    "{ \"Monday\": false,"
//                    + "\"Tuesday\": false,"
//                    + "\"Wednesday\": true,"
//                    + "\"Thursday\": false,"
//                    + "\"Friday\": false,"
//                    + "\"Saturday\": false,"
//                    + "\"Sunday\": true }"));
    }


    public void testPublishRide() throws Exception {
        String id = con.publish(offer);
        assertNotNull(offer.getRef());
        assertNotNull(id);
        con.search(new Ride().from(stuttgart).to(berlin).dep(new Date(later)));
        assertEquals("should find one", 1, con.getNumberOfRidesFound());
        Ride ride = con.ridesBatch.get(0);
        assertEquals("MX-123C", ride.get("NumberPlate"));
        con.printResults();
    }

    public void testBahn() throws Exception {
        con.publish(offer.mode(Mode.TRAIN));
        con.search(new Ride().from(stuttgart).to(berlin).dep(new Date(later)));
        con.printResults();
        assertEquals("should find one", 1, con.getNumberOfRidesFound());
        Ride ride = con.ridesBatch.get(0);
        assertEquals(Mode.TRAIN, ride.getMode());
    }


    public void testUpdateRide() throws Exception {
        con.publish(offer);
        offer = new Ride().type(Ride.OFFER).ref(offer.getRef()).activate()
            .dep(new Date(later + 3600000)).price(4242).seats(5)
            .from(leipzig).via(hannover).to(berlin)
            .set("Email", "foo")
            .set("Mobile", "0123")
            .set("Landline", "001")
            .set("NumberPlate", "MX")
            .set("Comment", "Hi there update..");
        offer.getDetails().put("Privacy", new JSONObject(
             "{ \"Name\": \"1\","       // anybody
             + "\"Landline\": \"1\","   // anybody
             + "\"Email\": \"1\","      // anybody
             + "\"Mobile\": \"1\","     // anybody
             + "\"NumberPlate\": \"1\" }"));
        offer.getDetails().put("Reoccur", new JSONObject(
                "{ \"Monday\": false,"
                + "\"Tuesday\": true,"
                + "\"Wednesday\": false,"
                + "\"Thursday\": false,"
                + "\"Friday\": true,"
                + "\"Saturday\": false,"
                + "\"Sunday\": false }"));
        con.publish(offer);
        con.search(new Ride().from(leipzig).to(berlin).dep(new Date(later)));
        assertEquals("should find one", 1, con.getNumberOfRidesFound());
        Ride ride = con.ridesBatch.get(0);
        assertEquals("MX", ride.get("NumberPlate"));
    }


    public void testContactVisibbility() throws Exception {
        con.publish(offer);
        con.login("wrong");
        con.search(new Ride().from(stuttgart).to(berlin).dep(new Date(later)));
        assertEquals("should find one", 1, con.getNumberOfRidesFound());
        Ride ride = con.ridesBatch.get(0);
        assertEquals("should match ref", offer.getRef(), ride.ref);
        assertEquals("public", "01234567", ride.details.getString("Mobile"));
        assertFalse("should not be visible", ride.details.has("Landline"));
        con.login("blabla");
        con.ridesBatch.clear();
        con.search(new Ride().from(stuttgart).to(berlin).dep(new Date(later)));
        ride = con.ridesBatch.get(0);
        assertEquals("members", "001234", ride.details.getString("Landline"));
    }

    @Override
    protected void tearDown() throws Exception {
        con.login("blabla");
        con.delete(offer);
        super.tearDown();
    }
}