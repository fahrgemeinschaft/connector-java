/**
 * Fahrgemeinschaft / Ridesharing App
 * Copyright (c) 2013 by it's authors.
 * Some rights reserved. See LICENSE.. 
 *
 */

package de.fahrgemeinschaft;

import java.util.Date;

import org.teleportr.Connector;
import org.teleportr.Place;

import junit.framework.TestCase;

public class Tests extends TestCase {

    private Connector connector;

    @Override
    protected void setUp() throws Exception {
        connector = new FahrgemeinschaftConnector();
        super.setUp();
    }

    Place berlin = new Place(52.519171, 13.406092);
    Place munich = new Place(48.1671, 11.6094);
    
    public void testSearchRides() {
        connector.getRides(munich, berlin, new Date(), null);
        connector.printResults();
    }
    
}
