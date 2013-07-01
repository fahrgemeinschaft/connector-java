/**
 * Fahrgemeinschaft / Ridesharing App
 * Copyright (c) 2013 by it's authors.
 * Some rights reserved. See LICENSE.. 
 *
 */

package de.fahrgemeinschaft;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.teleportr.Connector;
import org.teleportr.Place;

import junit.framework.TestCase;

public class Tests extends TestCase {

    private FahrgemeinschaftConnector connector;

    @Override
    protected void setUp() throws Exception {
        connector = new FahrgemeinschaftConnector();
        connector.prefs = new HashMap<String, String>();
        connector.prefs.put("radius_from", "15");
        connector.prefs.put("radius_to", "25");
        connector.prefs.put("username", "blablamail@gmx.net");
        connector.prefs.put("password", "blabla");
        connector.endpoint =  "http://test.service.fahrgemeinschaft.de";
        super.setUp();
    }

    public void testAuth() {
        connector.authenticate();
        assertNotNull("there should be some token", connector.getAuth());
    }

    Place berlin = new Place(52.519171, 13.406092);
    Place munich = new Place(48.1671, 11.6094);
    
    Place stuttgart = new Place(48.77541773021221, 9.181758686900139);
    Place muc_flughafen_nordallee = new Place(48.356820, 11.762299);
    
    public void testSearchRides() {
//        connector.getRides(munich, berlin, new Date(), null);
        connector.getRides(stuttgart, berlin,
                new Date(System.currentTimeMillis() + 24*3600000), null);
        connector.printResults();
    }
    
}
