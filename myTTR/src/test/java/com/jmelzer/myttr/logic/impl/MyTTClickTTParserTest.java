package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.logic.TestUtil;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by cicgfp on 26.11.2017.
 */

public class MyTTClickTTParserTest {
    final String ASSETS_DIR = "assets/mytt/clicktt";

    MyTTClickTTParserImpl parser = new MyTTClickTTParserImpl();

    @Test
    public void testparseLigaLinks() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-ligenplan.html");
        List<Liga> ligen = parser.parseLigaLinks(page);
        for (Liga liga : ligen) {
            System.out.println("liga = " + liga);
        }
    }

    @Test
    public void parseLinksBezirke() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-bezirke.html");
        List<Bezirk> bezirkList = parser.parseLinksBezirke(page);
        for (Bezirk bezirk : bezirkList) {
            System.out.println("bezirk = " + bezirk);
        }
    }

    @Test
    public void parseLiga() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-liga.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/aktuell");
        parser.parseLiga(page, liga);
        assertEquals(12, liga.getMannschaften().size());
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            System.out.println("mannschaft = " + mannschaft);
        }
    }

    @Test
    public void parseErgebnisse() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-spielplan.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/aktuell");

        parser.parseErgebnisse(page, liga, Liga.Spielplan.VR);
        assertEquals(11, liga.getSpieleVorrunde().size());
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel.getDate());
        }
    }

    @Test
    public void parseMannschaftsDetail() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschafts-detail.html");
        Mannschaft mannschaft = new Mannschaft();
        mannschaft.setUrl("https://www.mytischtennis.de/clicktt/");
        parser.parseMannschaftsDetail(page, mannschaft);
        assertEquals("Palapys, Alexander", mannschaft.getKontakt());
        assertEquals("alex@palapys.de", mannschaft.getMailTo());
        assertEquals("017672640971", mannschaft.getKontaktNr());
        assertEquals("Lindnerschule\n" +
                "Lindnerstr. 220\n" +
                "46149 Oberhausen", mannschaft.getSpielLokale().get(0));
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/verein/148033/SC-Buschhausen/info",
                mannschaft.getVereinUrl());
    }

    @Test
    public void parseMannschaftsDetail2() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschafts-detail2.html");
        Mannschaft mannschaft = new Mannschaft();
        mannschaft.setUrl("https://www.mytischtennis.de/clicktt/");
        parser.parseMannschaftsDetail(page, mannschaft);
        assertEquals("Hersel, Bernd", mannschaft.getKontakt());
        assertEquals("bhersel79@yahoo.de", mannschaft.getMailTo());
        assertEquals("0228-3361263", mannschaft.getKontaktNr());
        assertEquals("0177-2735179", mannschaft.getKontaktNr2());
        assertEquals(3, mannschaft.getSpielLokale().size());
        assertEquals("Turnhalle GS Rheidt\n" +
                "Hoher Rain (neben der Kirche)\n" +
                "53859 Niederkassel-Rheidt", mannschaft.getSpielLokale().get(0));
        assertEquals("Turnhalle der Grundschule\n" +
                "Martin-Buber-Straße\n" +
                "53859 Niederkassel", mannschaft.getSpielLokale().get(1));
        assertEquals("Sporthalle Nord am Kopernikus-Gymnasium Niederkassel\n" +
                "Premnitzerstr. Ecke Kopernikusstr.\n" +
                "53859 Niederkassel-Lülsdorf", mannschaft.getSpielLokale().get(2));
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/verein/156009/TTG-Niederkassel/info",
                mannschaft.getVereinUrl());
    }

    @Test
    public void parseMannschaftspiel() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschaftsspiel.html");
        Mannschaftspiel spiel = new Mannschaftspiel();
        spiel.setUrlDetail("http://bla.de" + spiel.getUrlDetail());
        parser.parseMannschaftspiel(page, spiel);
        for (Spielbericht spielbericht : spiel.getSpiele()) {
            System.out.println("spielbericht = " + spielbericht);
            assertNotEquals(spielbericht.getSpieler1Name(), spielbericht.getSpieler2Name());
            assertThat(spielbericht.getMyTTPlayerIdsForPlayer1().buildPopupUrl(), startsWith("http"));
            assertThat(spielbericht.getMyTTPlayerIdsForPlayer2().buildPopupUrl(), startsWith("http"));
            assertNotEquals(spielbericht.getMyTTPlayerIdsForPlayer1().buildPopupUrl(),
                    spielbericht.getMyTTPlayerIdsForPlayer2().buildPopupUrl());
        }
        System.out.println("spiel = " + spiel);
    }

    @Test
    public void parseLinksKreise() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-bezirk.html");
        List<Kreis> kreisList = parser.parseLinksKreise(page);
        assertEquals(6, kreisList.size());
        for (Kreis kreis : kreisList) {
            assertNotNull(kreis.getName());
            assertNotNull(kreis.getUrl());
        }
    }
    @Test
    public void parseLinksForPlayer() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/popover.html");
        Spieler spieler = parser.parseLinksForPlayer(page, "dummy");
        assertNotNull(spieler);
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/spieler/143001489/spielerportrait", spieler.getMytTTClickTTUrl());
        assertEquals("https://www.mytischtennis.de/community/events?personId=297020", spieler.getTtrHistorie());
        assertEquals("https://www.mytischtennis.de/community/headTohead?gegnerId=297020", spieler.getHead2head());
    }
    @Test
    public void parseSpieler() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/mytt-spieler.html");
        Spieler spieler = new Spieler("dummy");
        spieler = parser.parseSpieler(spieler, page);
        assertNotNull(spieler);
        assertThat(spieler.getClubName(), is("TTC Waldniel"));
        assertThat(spieler.getEinsaetze().get(0).getKategorie(), is("Herren 1"));
        assertThat(spieler.getEinsaetze().get(0).getLigaName(), is("Herren NRW-Liga 3"));
        assertThat(spieler.getEinsaetze().get(0).getUrl(),
                is("https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Herren-NRW-Liga-3/gruppe/305905/tabelle/aktuell"));
        assertThat(spieler.getEinsaetze().get(1).getKategorie(), is("Senioren 50 1"));
    }
}
