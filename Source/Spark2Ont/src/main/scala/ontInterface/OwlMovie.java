package ontInterface;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class OwlMovie {
    OWLOntology ont;
    PrefixManager pm;
    OWLOntologyManager manager;
    OWLDataFactory df;

    public OwlMovie() {
        try {
            pm = new DefaultPrefixManager(null, null, "https://www.kdm.com/OWL/movie#");
            manager = OWLManager.createOWLOntologyManager();
            df = manager.getOWLDataFactory();
            ont = initialzation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createSubClass(String className, String subClassName) {
        OWLClass baseClass = df.getOWLClass(className, pm);
        OWLClass subClass = df.getOWLClass(subClassName, pm);
        OWLSubClassOfAxiom declarationSubClassAxiom = df.getOWLSubClassOfAxiom(subClass, baseClass);
        manager.addAxiom(ont, declarationSubClassAxiom);
    }

    public void createClass(String className) {

        OWLClass classN = df.getOWLClass(className, pm);
        OWLDeclarationAxiom declarationAxiom = df.getOWLDeclarationAxiom(classN);
        manager.addAxiom(ont, declarationAxiom);

    }

    public void createIndividual(String individualName, String className) {
        OWLClass classN = df.getOWLClass(className, pm);
        OWLNamedIndividual ind = df.getOWLNamedIndividual(individualName, pm);
        OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(classN, ind);
        manager.addAxiom(ont, classAssertion);

    }

    public void createObjectProperty(String domain, String propertyName, String range) {

        OWLClass domainC = df.getOWLClass(domain, pm);
        OWLClass rangeC = df.getOWLClass(range, pm);
        OWLObjectProperty isPartof = df.getOWLObjectProperty(propertyName, pm);
        OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(isPartof, rangeC);
        OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(isPartof, domainC);
        manager.addAxiom(ont, rangeAxiom);
        manager.addAxiom(ont, domainAxiom);

    }

    public OWLOntology initialzation() throws Exception {
        //creating ontology manager
        //In order to create objects that represent entities we need a

        ont = manager.createOntology(IRI.create("https://www.kdm.com/OWL/", "movie"));
        OWLClass Movie = df.getOWLClass(":Movie", pm);
        OWLClass Moviecast = df.getOWLClass(":Moviecast", pm);
        OWLClass Movieplot = df.getOWLClass(":Movieplot", pm);

        OWLDeclarationAxiom declarationAxiomMovie = df.getOWLDeclarationAxiom(Movie);
        OWLDeclarationAxiom declarationAxiomMoviecast = df.getOWLDeclarationAxiom(Moviecast);
        OWLDeclarationAxiom declarationAxiomMovieplot = df.getOWLDeclarationAxiom(Movieplot);

        manager.addAxiom(ont, declarationAxiomMovie);
        manager.addAxiom(ont, declarationAxiomMoviecast);
        manager.addAxiom(ont, declarationAxiomMovieplot);

        //Making all classes Disjoint to each other
        OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(Movie, Moviecast, Movieplot);
        manager.addAxiom(ont, disjointClassesAxiom);

        //Creating Subclasses for Movieplot class

        //Creating Subclasses for Moviecast class
        OWLClass Malecast = df.getOWLClass(":Malecast", pm);
        OWLClass Femalecast = df.getOWLClass(":Femalecast", pm);
        OWLSubClassOfAxiom declarationAxiomMalecast = df.getOWLSubClassOfAxiom(Malecast, Moviecast);
        OWLSubClassOfAxiom declarationAxiomFemalecast = df.getOWLSubClassOfAxiom(Femalecast, Moviecast);
        manager.addAxiom(ont, declarationAxiomMalecast);
        manager.addAxiom(ont, declarationAxiomFemalecast);

        //Creating Subclasses for Malecast class
        OWLClass Leadactor = df.getOWLClass(":Leadactor", pm);
        OWLClass Supporactor = df.getOWLClass(":Supporactor", pm);
        OWLSubClassOfAxiom declarationAxiomLeadactor = df.getOWLSubClassOfAxiom(Leadactor, Malecast);
        OWLSubClassOfAxiom declarationAxiomSupporactor = df.getOWLSubClassOfAxiom(Supporactor, Malecast);
        manager.addAxiom(ont, declarationAxiomLeadactor);
        manager.addAxiom(ont, declarationAxiomSupporactor);

        //Creating Subclasses for Femalecast class
        OWLClass Leadactress = df.getOWLClass(":Leadactress", pm);
        OWLClass Supportactress = df.getOWLClass(":Supportactress", pm);
        OWLSubClassOfAxiom declarationAxiomSpicyLeadactress = df.getOWLSubClassOfAxiom(Leadactress, Femalecast);
        OWLSubClassOfAxiom declarationAxiomSupportactress = df.getOWLSubClassOfAxiom(Supportactress, Femalecast);
        manager.addAxiom(ont, declarationAxiomSpicyLeadactress);
        manager.addAxiom(ont, declarationAxiomSupportactress);


        //Creating Class Country and Individuals to classes
        OWLClass Country = df.getOWLClass(":Country", pm);
        OWLDeclarationAxiom declarationAxiomCountry = df.getOWLDeclarationAxiom(Country);
        OWLNamedIndividual India = df.getOWLNamedIndividual(":India", pm);
        OWLNamedIndividual USA = df.getOWLNamedIndividual(":USA", pm);
        OWLNamedIndividual UK = df.getOWLNamedIndividual(":UK", pm);
        //Class Assertion specifying India is member of Class Country
        OWLClassAssertionAxiom classAssertionIndia = df.getOWLClassAssertionAxiom(Country, India);
        OWLClassAssertionAxiom classAssertionUSA = df.getOWLClassAssertionAxiom(Country, USA);
        OWLClassAssertionAxiom classAssertionUK = df.getOWLClassAssertionAxiom(Country, UK);
        manager.addAxiom(ont, declarationAxiomCountry);
        manager.addAxiom(ont, classAssertionIndia);
        manager.addAxiom(ont, classAssertionUSA);
        manager.addAxiom(ont, classAssertionUK);

        //Creating Project class
        OWLClass Project = df.getOWLClass(":Project", pm);
        OWLDeclarationAxiom declarationAxiomProject = df.getOWLDeclarationAxiom(Project);
        manager.addAxiom(ont, declarationAxiomProject);

        //Creating Object Properties
        OWLObjectProperty isPartof = df.getOWLObjectProperty(":isPartof", pm);
        OWLObjectPropertyRangeAxiom rangeAxiomisPartof = df.getOWLObjectPropertyRangeAxiom(isPartof, Project);
        OWLObjectPropertyDomainAxiom domainAxiomisPartof = df.getOWLObjectPropertyDomainAxiom(isPartof, Project);
        manager.addAxiom(ont, rangeAxiomisPartof);
        manager.addAxiom(ont, domainAxiomisPartof);

        OWLObjectProperty has = df.getOWLObjectProperty(":has", pm);
        OWLObjectPropertyRangeAxiom rangeAxiomhas = df.getOWLObjectPropertyRangeAxiom(has, Project);
        OWLObjectPropertyDomainAxiom domainAxiomhas = df.getOWLObjectPropertyDomainAxiom(has, Project);
        manager.addAxiom(ont, rangeAxiomhas);
        manager.addAxiom(ont, domainAxiomhas);

        //Making isPartof and has inverse properties
        manager.addAxiom(ont, df.getOWLInverseObjectPropertiesAxiom(isPartof, has));

        //Creating hasCast, hasPlot Properties
        OWLObjectProperty hasCast = df.getOWLObjectProperty(":hasCast", pm);
        OWLObjectPropertyDomainAxiom domainAxiomhasCast = df.getOWLObjectPropertyDomainAxiom(hasCast, Movie);
        OWLObjectPropertyRangeAxiom rangeAxiomhasCast = df.getOWLObjectPropertyRangeAxiom(hasCast, Moviecast);
        manager.addAxiom(ont, rangeAxiomhasCast);
        manager.addAxiom(ont, domainAxiomhasCast);
        manager.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(hasCast, has));

        OWLObjectProperty hasPlot = df.getOWLObjectProperty(":hasPlot", pm);
        OWLObjectPropertyDomainAxiom domainAxiomhasPlot = df.getOWLObjectPropertyDomainAxiom(hasPlot, Movie);
        OWLObjectPropertyRangeAxiom rangeAxiomhasPlot = df.getOWLObjectPropertyRangeAxiom(hasPlot, Movieplot);
        manager.addAxiom(ont, rangeAxiomhasPlot);
        manager.addAxiom(ont, domainAxiomhasPlot);
        manager.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(hasPlot, has));

        //Making hasPlot property as Functional
        manager.addAxiom(ont, df.getOWLFunctionalObjectPropertyAxiom(hasPlot));

        //Making hasPlot property as Transitive
        manager.addAxiom(ont, df.getOWLTransitiveObjectPropertyAxiom(has));

        //Creating isCastOf, isPlotof Properties
        OWLObjectProperty isCastOf = df.getOWLObjectProperty(":isCastOf", pm);
        OWLObjectPropertyDomainAxiom domainAxiomisCastOf = df.getOWLObjectPropertyDomainAxiom(isCastOf, Moviecast);
        OWLObjectPropertyRangeAxiom rangeAxiomisCastOf = df.getOWLObjectPropertyRangeAxiom(isCastOf, Movie);
        manager.addAxiom(ont, domainAxiomisCastOf);
        manager.addAxiom(ont, rangeAxiomisCastOf);
        manager.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(isCastOf, isPartof));

        OWLObjectProperty isPlotof = df.getOWLObjectProperty(":isPlotof", pm);
        OWLObjectPropertyDomainAxiom domainAxiomisPlotof = df.getOWLObjectPropertyDomainAxiom(isPlotof, Movieplot);
        OWLObjectPropertyRangeAxiom rangeAxiomisPlotof = df.getOWLObjectPropertyRangeAxiom(isPlotof, Movie);
        manager.addAxiom(ont, domainAxiomisPlotof);
        manager.addAxiom(ont, rangeAxiomisPlotof);
        manager.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(isPlotof, isPartof));


        //Making isCastOf and hasCast inverse properties
        manager.addAxiom(ont, df.getOWLInverseObjectPropertiesAxiom(isCastOf, hasCast));

        //Making isPlotof and hasPlot inverse properties
        manager.addAxiom(ont, df.getOWLInverseObjectPropertiesAxiom(isPlotof, hasPlot));


        //Creating Data property
        OWLDataProperty hasVarieties = df.getOWLDataProperty(":hasVarieties", pm);
        OWLDatatype integerDatatype = df.getIntegerOWLDatatype();
        OWLDataPropertyDomainAxiom domainAxiomhasVarieties = df.getOWLDataPropertyDomainAxiom(hasVarieties, Country);
        OWLDataPropertyRangeAxiom rangeAxiomhasVarieties = df.getOWLDataPropertyRangeAxiom(hasVarieties, integerDatatype);
        manager.addAxiom(ont, domainAxiomhasVarieties);
        manager.addAxiom(ont, rangeAxiomhasVarieties);

        //Some values from Restriction
        OWLClassExpression hasPlotRestriction = df.getOWLObjectSomeValuesFrom(hasPlot, Movieplot);
        OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(Movie, hasPlotRestriction);
        manager.addAxiom(ont, ax);

        //Creating different kind of Movies
        OWLClass NamedMovie = df.getOWLClass(":NamedMovie", pm);
        OWLSubClassOfAxiom declarationAxiomNamedMovie = df.getOWLSubClassOfAxiom(NamedMovie, Movie);
        manager.addAxiom(ont, declarationAxiomNamedMovie);

        OWLClass JanathagarageMovie = df.getOWLClass(":JanathagarageMovie", pm);
        OWLSubClassOfAxiom declarationAxiomJanathagarageMovie = df.getOWLSubClassOfAxiom(JanathagarageMovie, NamedMovie);
        manager.addAxiom(ont, declarationAxiomJanathagarageMovie);

        OWLAnnotation commentAnno = df.getRDFSComment(df.getOWLLiteral("A Movie that only has Rama rao as a Lead actor", "en"));
        OWLAxiom commentJanathagarageMovie = df.getOWLAnnotationAssertionAxiom(JanathagarageMovie.getIRI(), commentAnno);
        manager.addAxiom(ont, commentJanathagarageMovie);

        OWLClassExpression hasCastRestriction = df.getOWLObjectSomeValuesFrom(hasCast, Leadactor);
        OWLSubClassOfAxiom axiomhasCastRestriction = df.getOWLSubClassOfAxiom(JanathagarageMovie, hasCastRestriction);
        manager.addAxiom(ont, axiomhasCastRestriction);

        OWLClassExpression hasCastRestrictionTomato = df.getOWLObjectSomeValuesFrom(hasCast, Leadactress);
        OWLSubClassOfAxiom axiomhasCastRestrictionTomato = df.getOWLSubClassOfAxiom(JanathagarageMovie, hasCastRestrictionTomato);
        manager.addAxiom(ont, axiomhasCastRestrictionTomato);

        return ont;
    }

    public void saveOntology() {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OutputStream os = new FileOutputStream("data/OwlMovie.owl");
            OWLXMLDocumentFormat owlxmlFormat = new OWLXMLDocumentFormat();
            manager.saveOntology(ont, owlxmlFormat, os);
            System.out.println("Ontology Created");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
