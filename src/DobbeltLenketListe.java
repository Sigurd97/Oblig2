/////////////////// class DobbeltLenketListe //////////////////////////////


// Navn : Sigurd Øvre Bjørndal
// Student nummer: s333741


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     * @param <T>
     */

    private static final class Node<T> {
        private T verdi;                   // Nodens verdi
        private Node<T> forrige, neste;    // Pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // Peker til den forste i listen
    private Node<T> hale;          // Peker til den siste i listen
    private Node<T> forrigeNode;
    private Node<T> currentNode;
    private int antall;            // Antall noder i listen
    private int endringer;         // Antall endringer i listen
    private int hodeIndex;         // Hjelpevariable for konstruktoren
    private int haleIndex;


    // hjelpemetode
    private Node finnNode(int indeks) throws IndexOutOfBoundsException {

        int teller;

        if (indeks < (antall / 2)) {     // Starter  leting ved hode og mot hoyre

            Node current = hode;
            teller = 0;

            while (current != null) {   // Kjorer gjennom listen til den treffer null som kommer etter hale, men returnerer for det.
                if (teller == indeks) {
                    return current;
                }
                teller++;
                current = current.neste;
            }
        } else {                        // Leting starter ved hale og mot venstre

            Node current = hale;
            teller = antall - 1;        // Telleren maa ha antall minus 1 fordi man teller med '0' som forste.

            while (current != null) {   // Kjorer gjennom hele listen baklengs til den treffer null som kommer foer hode, men returnerer for det.
                if (teller == indeks) {
                    return current;
                }
                teller--;
                current = current.forrige;
            }
        }
        return null;                    // Returnerer null dersom ikke funnet.
    }


    public DobbeltLenketListe() {

    }

    public DobbeltLenketListe(T[] a) {
        //warning "Method will throw an exception when parameter is null, men i oopgaven staar det at metoden skal gjoere dette.
        Objects.requireNonNull(a, "Tabellen er Null!");
        if (a.length==0) {                                        // Spesialtilfelle der tabellen a er tom men ikke null
            hode = null;
            hale = null;
        } else if (a.length==1){                                  // Spesialtilfelle der tabellen a kun har 1 element
            if (a[0]==null){
                hode = null;
                hale = null;
            } else {
                hode = new Node<>(a[0]);
                hale = hode;
                antall ++;
            }
        } else {                                                 //hvis tabellen har flere enn ett ikke-null elementer gaar vi videre.
            //setter verdi for hode, forste ikke-null element i tabellen.
            for (int i = 0; i < a.length; i++) {
                if (a[i] != null) {
                    hode = new Node<>(a[i], null, null);
                    hodeIndex = i;
                    forrigeNode = hode;
                    antall++;
                    break;
                }
            }
            //setter verdi for hale, siste ikke-null element i tabellen.
            for (int i = a.length - 1; i > hodeIndex; i--) {
                if (a[i] != null) {
                    hale = new Node<>(a[i], null, null);
                    haleIndex = i;
                    antall++;
                    break;
                }
            }
            //instansierer noder for resten av de ikke null elementene i tabellen a hvis de eksisterer.
            for (int i = hodeIndex + 1; i < haleIndex; i++) {
                if (a[i] != null) {
                    assert forrigeNode != null; //noe compileren gjorde for aa fjerne warning
                    forrigeNode.neste = new Node<>(a[i], forrigeNode, null);
                    forrigeNode = forrigeNode.neste;
                    antall++;
                }
            }
            if (antall == 0) {                          //hvis antall fortsatt er null besto tabellen kun av null elementer.
                hode = null;
                hale = null;
            } else if (antall==1){                      //hvis antall er 1 her betyr det at tabellen hadde flere enn 1 element, men kun ett av dem var ikke-null.
                hale = hode;
            } else {                                    //setter neste peker til hale i nest siste node.
                assert forrigeNode != null;             //for aa fjerne warning, aner ikke hva det gjor.
                forrigeNode.neste = hale;
                assert hale != null;
                hale.forrige = forrigeNode;
            }
        }
    }

    public Liste<T> subliste(int fra, int til) {        // Lager en  liste fra og med indeks 'fra' til men ikke inkludert indeks 'til'.

        fratilKontroll(antall, fra, til);               // Skjekker at indeksene har lovlige verdier.

        DobbeltLenketListe<T> nyListe = new DobbeltLenketListe<>();
        currentNode = hode;
        int teller = 0;

        while (currentNode != null) {                     // Kjorer gjennom hele listen til den blir stoppet, eller gaar forbi hale.
            if (teller >= fra) {
                if (teller == til) {return nyListe;}
                nyListe.leggInn(currentNode.verdi);
            }
            teller++;
            currentNode = currentNode.neste;
        }
        return nyListe;
    }

    private static void fratilKontroll(int antall, int fra, int til){
        if (fra < 0) {throw new IndexOutOfBoundsException ("fra(" + fra + ") er negativ!");}
        if (til > antall) {throw new IndexOutOfBoundsException ("til(" + til + ") > antall noder(" + antall + ")");}
        if (fra > til) {throw new IllegalArgumentException ("fra(" + fra + ") > til(" + til + ") - illegalt intervall!");}
    }

    @Override
    public int antall() { //antallet ikke-null noder i listen.
        return antall;
    }

    @Override
    public boolean tom() { //er listen tom for ikke-null noder?
        return antall == 0;
    }

    @Override
    public boolean leggInn(T verdi) { //metode for aa legge til verdi paa slutten av listen.
        //warning "Method will throw an exception when parameter is null, men i oopgaven staar det at metoden skal gjoere dette.
        Objects.requireNonNull(verdi, "Null verdier ikke tillatt.");
        if (hode == null && hale == null && antall == 0) {
            hode = new Node<>(verdi);
            hale = hode;
        } else {
            assert hale != null;
            hale.neste = new Node<>(verdi, hale, null); //instansierer ny node
            hale = hale.neste; //setter "hale" til aa peke paa denne.
        }
        antall++;
        endringer++;
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        //warning "Method will throw an exception when parameter is null, men i oopgaven staar det at metoden skal gjoere dette.
        Objects.requireNonNull(verdi, "Null verdier ikke tillatt.");
        int teller=0;
        if (indeks<0 || indeks>antall) { //antall vil alltid vaere siste indeks + 1
            throw new IndexOutOfBoundsException();
        }
        if (antall==0){ //tom liste
            hale=new Node<>(verdi,null,null);
            hode = hale;
        } else if (indeks == 0) { //element skal legges inn paa indeks 0.
            hode = new Node<>(verdi, null, hode);
            hode.neste.forrige = hode;
        } else if (indeks == antall) { //element skal legges inn paa siste indeks.
            hale = new Node<>(verdi, hale, null);
            hale.forrige.neste = hale;
        } else{
            assert finnNode(indeks) != null;
            Node<T> nyNode = new Node<T>(verdi,finnNode(indeks).forrige,finnNode(indeks));
            nyNode.forrige.neste = nyNode;
            nyNode.neste.forrige = nyNode;
        }
        antall++;
        endringer++;
    }

    @Override
    public boolean inneholder(T verdi) {
        int indexTil = indeksTil(verdi);
        return indexTil != -1;
    }

    @Override
    public T hent(int indeks) { // Henter element paa plass indeks.
        indeksKontroll(indeks,false);
        if (indeks >= antall) {throw new IndexOutOfBoundsException("Listen har kun " + antall + " antall elementer. Indeks " + indeks + " er for hoy.");}
        if (tom() || indeks < 0) {throw new IndexOutOfBoundsException("Listen er tom, denne indeksen finnes ikke");}
        Node funnetNode = finnNode(indeks);
        return (T) funnetNode.verdi;

    }

    @Override
    public int indeksTil(T verdi) {
        int index = 0;
        if (hode == null) {return -1;}
        if (hode.verdi.equals(verdi)) {
            return index;
        }
        index++;
        forrigeNode = hode;
        while (forrigeNode != hale) {
            if (forrigeNode.neste.verdi.equals(verdi)) {
                return index;
            } else {
                forrigeNode = forrigeNode.neste;
                index++;
            }
        }
        if (hale.verdi.equals(verdi)) {
            return index;
        } else {return -1;}
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        //warning "Method will throw an exception when parameter is null, men i oopgaven staar det at metoden skal gjoere dette.
        if ((indeks < 0) || (indeks > antall)) { throw new IndexOutOfBoundsException("Indeks " + indeks + " er ikke gyldig.");}
        indeksKontroll(indeks, false);
        if (nyverdi == null) { throw new NullPointerException("Nyverdi kan ikke vaere 'null'");}

        T gammelVerdi;
        int teller = 0;
        currentNode = hode;

        while (teller < antall) {       // Gaar gjennom hele listen
            if (teller == indeks) {     // Finner rett indeks
                gammelVerdi = currentNode.verdi;    // Tar var paa gammel verdi som ellers blir overskrevet i neste linje.
                currentNode.verdi = nyverdi;
                endringer++;
                return gammelVerdi;     // Returnerer verdien her naar funnet, ikke lenger noen poeng aa fortsette gjennom hele listen.
            }
            currentNode = currentNode.neste;
            teller++;
        }
        return null;
    }

    @Override
    public boolean fjern(T verdi) {
        if (antall == 0) {return false;}
        if (antall == 1) {
            if (hode.verdi.equals(verdi)) {
                hode = null;
                hale = null;
                antall--;
                endringer++;
                return true;
            }
            else {return false;}
        }
        forrigeNode = hode;
        while (forrigeNode != hale) {
            if (forrigeNode.verdi.equals(verdi)) {
                try {
                    forrigeNode.forrige.neste = forrigeNode.neste;
                } catch (NullPointerException e) {
                    hode = hode.neste;
                    hode.forrige = null;
                }
                forrigeNode.neste.forrige = forrigeNode.forrige;
                antall--;
                endringer++;
                return true;
            }
            forrigeNode = forrigeNode.neste;
        }
        if (hale.verdi.equals(verdi)) {
            hale = hale.forrige;
            hale.neste = null;
            antall--;
            endringer++;
            return true;
        }
        return false;
    }

    @Override
    public T fjern(int indeks) {
        if (antall == 0) {throw new IndexOutOfBoundsException("listen er tom, denne indeksen finnes ikke");}
        if (indeks < 0 || indeks >= antall) {throw new IndexOutOfBoundsException();}
        T verdi;
        forrigeNode = hode;
        for (int i = 0; i < indeks; i++) {
            forrigeNode = forrigeNode.neste;
        }
        verdi = forrigeNode.verdi;
        try {           //er elementet hale?
            forrigeNode.neste.forrige = forrigeNode.forrige;
        } catch (NullPointerException e) {
            try {       //har listen kun en Node?
                hale = hale.forrige;
                hale.neste = null;
            }catch (NullPointerException E) { //hverken forrige eller neste finnes
                hale = null;
                hode = null;
                antall--;
                endringer++;
                return verdi;
            }
        }
        try { //er elementet hode?
            forrigeNode.forrige.neste = forrigeNode.neste;
        }catch (NullPointerException e) {
            hode = hode.neste;
            hode.forrige = null;
        }
        antall--;
        endringer++;
        return verdi;

    }

    @Override
    public void nullstill() {

        long startTime = System.nanoTime();

        // Metode 1
        currentNode = hode.neste;
        while (currentNode.neste != hale) { // Fjerner alt mellom hode og hale
            currentNode.forrige.forrige = null;
            currentNode.forrige = null;
            currentNode.verdi = null;
            currentNode = currentNode.neste;
        }

        hode.neste = null; // Nuller ut hode
        hode.verdi = null;

        hale.forrige = null; // Nuller ut hale
        hale.verdi = null;

        fjern(0); // Fjerner siste null-element

        antall = 0;

        // Metode to
        /*
        while (antall != 0){
            fjern(0);
        }
        */

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Nanosekunder: " + timeElapsed);
        // Metode 1 utfortes paa 67698 nanosekunder
        // Metode 2 utfortes paa 210489 nanosekunder, betraktelig mye lenger tid.


    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (hode!=null) {
            str.append(hode.verdi);
        } else {return("[]");}
        forrigeNode = hode;
        while (forrigeNode.neste!=null) {
            str.append(", ");
            str.append(forrigeNode.neste.verdi);
            forrigeNode = forrigeNode.neste;
        }
        str.append("]");
        return str.toString();
    }

    public String omvendtString() {
        StringBuilder omvstr = new StringBuilder();
        omvstr.append("[");
        if (hale != null) {
            omvstr.append(hale.verdi);
        } else {return("[]");}
        forrigeNode = hale;
        while (forrigeNode.forrige != null) {
            omvstr.append(", ");
            omvstr.append(forrigeNode.forrige.verdi);
            forrigeNode = forrigeNode.forrige;
        }
        omvstr.append("]");
        return omvstr.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks) {
        indeksKontroll(indeks,false);
        return new DobbeltLenketListeIterator(indeks);
    }

    private class DobbeltLenketListeIterator implements Iterator<T> {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator() {
            denne = hode;     // p starter paa den forste i listen
            fjernOK = false;  // blir sann naar next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks){
            denne = finnNode(indeks);   //starter paa indeks
            fjernOK = false;  // blir sann naar next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        @Override
        public boolean hasNext() {
            return (denne != null);
        }

        @Override
        public T next() {
            T verdi;
            if (endringer != iteratorendringer) {
                throw new ConcurrentModificationException();
            } else if (!hasNext()){
                throw new NoSuchElementException();
            }
            fjernOK = true;
            verdi = denne.verdi;
            denne = denne.neste;
            return verdi;
        }

        @Override
        public void remove() {
            IllegalStateException e = new IllegalStateException();
            if (antall == 0 || !fjernOK) {
                throw e;
            }
            if (endringer != iteratorendringer) {
                throw new ConcurrentModificationException();
            }
            fjernOK = false;
            if (antall == 1) {
                hode = null;
                hale = null;
            } else if (denne == null) {
                hale = hale.forrige;
                hale.neste = null;
            } else if (denne.forrige == hode) {
                hode = hode.neste;
                hode.forrige = null;
            } else {
                denne.forrige.forrige.neste = denne;
                denne.forrige = denne.forrige.forrige;
            }
            iteratorendringer++;
            endringer++;
            antall--;
        }
    } // class DobbeltLenketListeIterator

    private static <T> void bytt(Liste<T> liste, int i, int j){
        // Legger til variabler
        T minst = liste.hent(i);
        T storst = liste.hent(j);

        // Foretar bytting ved innplassering i liste
        liste.oppdater(i, storst);
        liste.oppdater(j, minst);
    }

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        long startTime = System.nanoTime();

        for (int i = 0; i < liste.antall(); i++) {          // Kjorer gjennom alle elementene.
            for (int j = 0; j < liste.antall(); j++) {      // Kjorer gjennom alle elementene en gang til til sammenlikning.
                if ((c.compare(liste.hent(i), liste.hent(j))) < 0) {      // Gir true naar i < j, for da blir det -1 < 0.
                    bytt(liste, i, j);                      // Bytter
                }
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Nanosekunder: " + timeElapsed);
    }
} // class DobbeltLenketListe


