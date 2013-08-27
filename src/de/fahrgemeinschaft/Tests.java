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
    Place leipzig = new Place("u30u1d1g3sc").address("Leipzig, Deutschland");
    Place nürnberg = new Place("u0zck43yfyx").address("Nürnberg, Deutschland");
    
    Place stuttgart = new Place(48.775417, 9.181758).address("Stuttgart, Deutschland");
    Place muc_flughafen_nordallee = new Place(48.356820, 11.762299);
    
    public void testSearchRides() throws Exception {
        con.search(stuttgart, berlin,
                new Date(System.currentTimeMillis() + 24*3600000), null);
        con.printResults();
    }

    public void testWrongApiKey() throws Exception {
        try {
            Secret.APIKEY = "wrong";
            con.search(stuttgart, berlin,
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
            .dep(new Date()).price(4200)
            .from(stuttgart).via(munich).via(leipzig).to(berlin)
            .set("EMail", "foo@bar.baz") // with upcase 'M' !
            .set("Mobile", "01234567")
            .set("Landline", "001234")
            .set("NumberPlate", "MX-123C")
            .set("Comment", "Hi there..");
        offer.getDetails().put("Privacy", new JSONObject(
             "{ \"Name\": \"0\","       // request
             + "\"Landline\": \"4\","   // members
             + "\"Email\": \"5\","      // nobody   with lowcase 'm' !
             + "\"Mobile\": \"1\","     // anybody
             + "\"NumberPlate\": \"1\" }"));
        offer.getDetails().put("Reoccur", new JSONObject(
                "{ \"Monday\": false,"
                + "\"Tuesday\": false,"
                + "\"Wednesday\": true,"
                + "\"Thursday\": false,"
                + "\"Friday\": false,"
                + "\"Saturday\": false,"
                + "\"Sunday\": true }"));
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
        Ride offer = new Ride().ref("aebe362a-a6bc-6eb4-4d56-535dc8e99693")
            .dep(new Date()).price(4242).seats(5)
            .from(stuttgart).via(leipzig).to(berlin)
            .set("EMail", "foo")
            .set("Mobile", "0123")
            .set("Landline", "001")
            .set("NumberPlate", "MX")
            .set("Comment", "Hi there update..");
        offer.getDetails().put("Privacy", new JSONObject(
             "{ \"Name\": \"1\","       // request
             + "\"Landline\": \"1\","   // members
             + "\"Email\": \"1\","      // nobody
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
        Ride offer = new Ride().ref("d206d346-70be-3ec4-91f7-b02308c8f069");
        con.delete(offer);
    }
}