/**
 * Fahrgemeinschaft / Ridesharing App
 * Copyright (c) 2013 by it's authors.
 * Some rights reserved. See LICENSE..
 *
 */

package de.fahrgemeinschaft;

import java.util.Date;

import org.teleportr.AuthException;
import org.teleportr.Ride;

public class TestDownload extends Tests {

    @Override
    protected void setUp() throws Exception {
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

    public void testSearchRides() throws Exception {
        con.search(new Ride().type(Ride.SEARCH).from(füssing).to(munich)
                .dep(new Date(System.currentTimeMillis() + 1*24*3600000)));
        con.printResults();
    }

    public void testWrongApiKey() throws Exception {
        try {
            Secret.APIKEY = "wrong";
            con.search(new Ride().type(Ride.SEARCH).from(hamburg).to(hannover)
                    .dep(new Date(System.currentTimeMillis() + 0*24*3600000)));
            assertNotNull("should not happen", null);
        } catch (AuthException e) {
            assertNull("should happen", null);
        }
    }

    public void testGetMyRides() {
        try {
            con.login("blabla");
            con.search(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.printResults();
    }

}