package SentenceSimplification;

import edu.stanford.nlp.trees.Tree;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Wrapper {

    public static void main(String[] args) {
        String document = "passage.txt";

        //generate parse trees for each sentence in the document
        ParseTreeGenerator parseTreeGenerator = new ParseTreeGenerator();
        List<Tree> parseTrees = parseTreeGenerator.getParseTreesForDocument(document);

        //perform pronoun noun phrase coreference resolution using arkref
        /*CoreferenceResolver coreferenceResolver = new CoreferenceResolver();
        coreferenceResolver.resolveCorefence(parseTrees);*/

        //perform sentence simplification
        SentenceSimplifier sentenceSimplifier = new SentenceSimplifier();
        List<Question> trees = new ArrayList<>();
        Collection<Question> tmpSet;
        //Supersense for where:



        int sentnum = 0;
        for (Tree sentence : parseTrees) {
            if (AnalysisUtilities.filterOutSentenceByPunctuation(AnalysisUtilities.orginialSentence(sentence.yield()))) {
                sentnum++;
                continue;
            }

            tmpSet = sentenceSimplifier.simplify(sentence, false);
            /*for (Question q : tmpSet) {
                q.setSourceSentenceNumber(sentnum);
                q.setSourceDocument(coreferenceResolver.getDocument());
            }*/
            trees.addAll(tmpSet);

            sentnum++;
        }

        //add new sentences with clarified/resolved NPs
        /*trees.addAll(coreferenceResolver.clarifyNPs(trees, true, true));*/


        StringBuilder sb = new StringBuilder();
        //upcase the first tokens of all output trees.
        for (Question q : trees) {
            AnalysisUtilities.upcaseFirstToken(q.getIntermediateTree());
            List<Tree> simplifiedSentence = q.getIntermediateTree().getLeaves();
            for (Tree s : simplifiedSentence) {
                sb.append(s.toString());
                sb.append(" ");
            }
            sb.append("\n");
        }
        try {
            File file = new File("simplifiedSentences.txt");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(sb.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //generate how and why questions
        QuestionGenerator questionGenerator = new QuestionGenerator();
        List<QuestionAnswer> howAndWhyQuestions = questionGenerator.generateQuestions(trees);
        Set<QuestionAnswer> withoutduplicates = new HashSet<>(howAndWhyQuestions);
        howAndWhyQuestions.clear();
        howAndWhyQuestions.addAll(withoutduplicates);
        for(QuestionAnswer qa: howAndWhyQuestions){
            System.out.println("Question:"+qa.getQuestion()+" Answer:"+qa.getAnswer()+" Sentence:"+qa.getSentence());

        }

        }
}
