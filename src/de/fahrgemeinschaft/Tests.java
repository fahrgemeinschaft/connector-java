/**
 * Fahrgemeinschaft / Ridesharing App
 * Copyright (c) 2013 by it's authors.
 * Some rights reserved. See LICENSE..
 *
 */

package de.fahrgemeinschaft;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.teleportr.AuthException;
import org.teleportr.Place;
import org.teleportr.Ride;
import org.teleportr.Ride.Mode;

public class Tests extends TestCase {

    private FahrgemeinschaftConnector con;

    @Override
    protected void setUp() throws Exception {
        con = new FahrgemeinschaftConnector();
        con.settings = new HashMap<String, String>();
        con.settings.put("radius_from", "15");
        con.settings.put("radius_to", "25");
        con.settings.put("login", "blablamail@gmx.net");
        con.endpoint =  "http://test.service.fahrgemeinschaft.de";
        super.setUp();
    }

    public void testAuth() throws Exception {
        try {
            con.authenticate("wrong");
            assertNull("should not happen");
        } catch (AuthException e) {
            assertNotNull("should happen");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(con.authenticate("blabla"));
        assertEquals("bla", con.get("firstname"));
        assertEquals("b.", con.get("lastname"));
        assertEquals("f9bb1cab-9793-8534-5d01-58c3d27d50fc", con.get("user"));
        con.login("blabla");
        assertNotNull(con.getAuth());
        try {
            con.login("wrong again");
        } catch (Exception e) {}
        assertNull(con.getAuth());
    }

    Place berlin = new Place(52.519171, 13.406092).address("Berlin, Deutschland");
    Place munich = new Place(48.1671, 11.6094).address("München, Deutschland");
    Place augsburg = new Place(48.3667, 10.9000).address("Augsburg, Deutschland");
    Place hamburg = new Place(53.5653, 10.0014).address("Hamburg, Deutschland");
    Place hannover = new Place(52.3667, 9.7167).address("Hannover, Deutschland");
    Place leipzig = new Place("u30u1d1g3sc").address("Leipzig, Deutschland");
    Place nürnberg = new Place("u0zck43yfyx").address("Nürnberg, Deutschland");
    
    Place stuttgart = new Place(48.775417, 9.181758).address("Stuttgart, Deutschland");
    Place muc_flughafen_nordallee = new Place(48.356820, 11.762299);
    
    public void testSearchRides() throws Exception {
        con.search(hamburg, hannover,
                new Date(System.currentTimeMillis() + 0*24*3600000), null);
        con.printResults();
    }

    public void testWrongApiKey() throws Exception {
        try {
            Secret.APIKEY = "wrong";
            con.search(hamburg, hannover,
                    new Date(System.currentTimeMillis() + 24*3600000), null);
            assertNotNull("should not happen", null);
        } catch (AuthException e) {
            assertNull("should happen", null);
        }
    }

    public void testGetMyRides() {
        try {
            con.login("blabla");
            con.search(null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.printResults();
    }

    public void testPublishRide() throws Exception {
        con.login("blabla");
        Ride offer = new Ride().activate()
            .dep(new Date()).price(0)
            .from(hamburg).via(munich).via(leipzig).to(hannover)
            .set("Email", "foo@bar.baz")
            .set("Mobile", "01234567")
            .set("Landline", "001234")
            .set("NumberPlate", "MX-123C")
            .set("Comment", "Hi there..");
        offer.getDetails().put("Privacy", new JSONObject(
             "{ \"Name\": \"5\","       // nobody
             + "\"Landline\": \"4\","   // members
             + "\"Email\": \"1\","      // anybody
             + "\"Mobile\": \"0\","     // request
             + "\"NumberPlate\": \"0\" } "));
//        offer.getDetails().put("Reoccur", new JSONObject(
//                "{ \"Monday\": false,"
//                + "\"Tuesday\": false,"
//                + "\"Wednesday\": true,"
//                + "\"Thursday\": false,"
//                + "\"Friday\": false,"
//                + "\"Saturday\": false,"
//                + "\"Sunday\": true }"));
        String id = con.publish(offer);
        System.out.println(id);
        // go to test.fahrgemeinschaft and assert published
    }

    public void testBahn() throws Exception {
        Ride offer = new Ride().dep(new Date()).price(4200).mode(Mode.TRAIN)
            .from(stuttgart).via(munich).via(leipzig).to(berlin).activate();
        String id = con.publish(offer);
        System.out.println(id);
        con.search(stuttgart, berlin,
                new Date(System.currentTimeMillis() - 2*3600000), null);
        con.printResults();
    }


    public void testUpdateRide() throws Exception {
        Ride offer = new Ride().ref("071c86c5-2631-85c4-2941-890da72bf486")
            .dep(new Date()).price(4242).seats(5)
            .from(stuttgart).via(leipzig).to(berlin)
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
    }

    public void testDeleteRide() throws Exception {
        con.login("blabla");
        Ride offer = new Ride().ref("618bf962-b45e-7394-6989-63e70d30b76b");
        con.delete(offer);
    }

    public void testContactVisibility() throws Exception {
        long later = System.currentTimeMillis() + 300*24*3600000;
        con.login("blabla");
        Ride offer = new Ride().activate()
            .dep(new Date(later)).price(4200)
            .from(stuttgart).via(munich).via(leipzig).to(berlin)
            .set("Email", "foo@bar.baz")
            .set("Mobile", "01234567")
            .set("Landline", "001234");
        offer.getDetails().put("Privacy", new JSONObject(
             "{ \"Name\": \"0\","       // request
             + "\"Landline\": \"4\","   // members
             + "\"Email\": \"5\","      // nobody
             + "\"Mobile\": \"1\","     // anybody
             + "\"NumberPlate\": \"1\" }"));
        String id = con.publish(offer);
        assertNotNull("should be published", id);

        con.login("wrong");
        con.search(stuttgart, berlin, new Date(later), null);
        assertEquals("should find one", 1, con.getNumberOfRidesFound());
        Ride ride = con.ridesBatch.get(0);
        assertEquals("should match ref", id, ride.ref);
        assertEquals("public", "01234567", ride.details.getString("Mobile"));
        assertFalse("should not be visible", ride.details.has("Landline"));

        con.login("blabla");
        con.ridesBatch.clear();
        con.search(stuttgart, berlin, new Date(later), null);
        ride = con.ridesBatch.get(0);
        assertEquals("members", "001234", ride.details.getString("Landline"));

        con.delete(new Ride().ref(id)); // clean up
    }
}