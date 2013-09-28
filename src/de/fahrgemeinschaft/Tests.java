/**
 * Fahrgemeinschaft / Ridesharing App
 * Copyright (c) 2013 by it's authors.
 * Some rights reserved. See LICENSE..
 *
 */

package de.fahrgemeinschaft;

import java.util.HashMap;

import junit.framework.TestCase;

import org.teleportr.Place;

public class Tests extends TestCase {

    FahrgemeinschaftConnector con;

    @Override
    protected void setUp() throws Exception {
        con = new FahrgemeinschaftConnector();
        con.settings = new HashMap<String, String>();
        con.settings.put("radius_from", "15");
        con.settings.put("radius_to", "25");
        con.settings.put("login", "blablamail@gmx.net");
        con.endpoint =  "http://service.fahrgemeinschaft.de";
        super.setUp();
    }


    Place berlin = new Place(52.519171, 13.406092).address("Berlin, Deutschland");
    Place munich = new Place(48.1671, 11.6094).address("München, Deutschland");
    Place augsburg = new Place(48.3667, 10.9000).address("Augsburg, Deutschland");
    Place hamburg = new Place(53.5653, 10.0014).address("Hamburg, Deutschland");
    Place hannover = new Place(52.3667, 9.7167).address("Hannover, Deutschland");
    Place leipzig = new Place("u30u1d1g3sc").address("Leipzig, Deutschland");
    Place nürnberg = new Place("u0zck43yfyx").address("Nürnberg, Deutschland");
    Place füssing = new Place(48.3500, 13.3000).address("Bad Füssing, Deutschland");
    
    Place stuttgart = new Place(48.775417, 9.181758).address("Stuttgart, Deutschland");
    Place muc_flughafen_nordallee = new Place(48.356820, 11.762299);

}