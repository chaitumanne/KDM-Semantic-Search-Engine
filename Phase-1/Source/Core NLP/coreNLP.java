import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Created by Venkatesh on 22-Jun-16.
 */
public class Main {
    public static void main(String args[]) {
        List lem = new ArrayList();
        List ps= new ArrayList();
        List ner = new ArrayList();
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

// read some text in the text variable
        String text = "The third movie produced by Howard Hughes, this gem was thought to be lost. It was recently restored and shown on TCM (12/15/04). The plot is a familiar one - two WW I soldiers escape from a German prison camp (guarded by an extremely lethargic German shepherd, who practically guides them out of the camp), stow away on a ship, and end up in \"Arabia\", where they rescue the lovely Mary Astor. The restoration is very good overall, although there are two or three very rough sequences. The production is very good, and there are some very funny scenes. And did I mention that Mary Astor is in it? The film won an Academy Award for the now-defunct category of \"Best Direction of a Comedy\"."; // Add your text here!

// create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

// run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            System.out.println("\n"+ sentence);
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {



                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);

                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                lem.add(lemma);

                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                ps.add("("+ word +": "+ pos + ")");

                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                ner.add("("+ word +": "+ ne + ")");

            }


            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println(tree);
            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            System.out.println(dependencies.toString());

            Map<Integer, CorefChain> graph =
                    document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            System.out.println(graph.values().toString());
        }
        System.out.println(lem);
        System.out.println(ps);
        System.out.println(ner);



    }
}
