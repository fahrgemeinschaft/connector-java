/**
 * Fahrgemeinschaft / Ridesharing App
 * Copyright (c) 2013 by it's authors.
 * Some rights reserved. See LICENSE..
 *
 */

package de.fahrgemeinschaft;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.teleportr.Place;
import org.teleportr.Ride;

public class Tests extends TestCase {

    private FahrgemeinschaftConnector connector;

    @Override
    protected void setUp() throws Exception {
        connector = new FahrgemeinschaftConnector();
        connector.settings = new HashMap<String, String>();
        connector.settings.put("radius_from", "15");
        connector.settings.put("radius_to", "25");
        connector.settings.put("username", "blablamail@gmx.net");
        connector.settings.put("password", "blabla");
        connector.endpoint =  "http://test.service.fahrgemeinschaft.de";
        super.setUp();
    }

    public void testAuth() {
        String auth = connector.getAuth();
        System.out.println(auth);
        assertNotNull(auth);
    }

    Place berlin = new Place(52.519171, 13.406092).address("Berlin");
    Place munich = new Place(48.1671, 11.6094).address("München");
    
    Place stuttgart = new Place(48.775417, 9.181758).address("Stuttgart");
    Place muc_flughafen_nordallee = new Place(48.356820, 11.762299);
    
    public void testSearchRides() {
//        connector.getRides(munich, berlin, new Date(), null);
        connector.search(stuttgart, berlin,
                new Date(System.currentTimeMillis() + 24*3600000), null);
        connector.printResults();
    }
    
    public void testPublishRide() throws Exception {
        connector.publish(new Ride().from(stuttgart).to(berlin));
        // go to test.fahrgemeinschaft and assert published
    }

    public void testPublishSubRides() throws Exception {
        Ride offer = new Ride().from(stuttgart).via(munich).to(berlin);
        connector.publish(offer);
        // go to test.fahrgemeinschaft and assert published
    }
}
