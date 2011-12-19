package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for both GelFreeIdentification and TwoDimIdentification
 * ToDo: this is a replica of PRIDE XML data model, additional support is needed for mzIdentML
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:21:57
 */
public class Identification extends IdentifiableParamGroup {

    /**
     * DEB Sequence
     */
    private DBSequence dbSequence = null;

    /**
     * Pass Threshold of the Search Engine
     */
    private boolean passThreshold = false;

    /**
     * Peptide Evidence in which is based the identification
     */
    private Map<PeptideEvidence, List<Peptide>> peptides = null;

    /**
     * The score is the score value in a SearchEngine Context
     */

    private Score score = null;

    /**
     * percentage of sequence coverage obtained through all identified peptides/masses
     */
    private double sequenceCoverage = -1;

    /**
     * optional search engine threshold
     */
    private double threshold = -1;

    /**
     * Gel related details
     */
    private Gel gel = null;

    /**
     * The group in wich we can find the Protein Identification
     */
    IdentifiableParamGroup proteinAmbiguityGroup = null;

    public Identification(Comparable id, String name, DBSequence dbSequence, boolean passThreshold,
                          Map<PeptideEvidence, List<Peptide>> peptides, Score score, double threshold, double sequenceCoverage, Gel gel) {
        this(null, id, name, dbSequence, passThreshold, peptides, score, threshold, sequenceCoverage, gel);
    }

    public Identification(ParamGroup params, Comparable id, String name, DBSequence dbSequence, boolean passThreshold,
                          Map<PeptideEvidence, List<Peptide>> peptides, Score score, double threshold, double sequenceCoverage, Gel gel) {
        super(params, id, name);
        this.dbSequence       = dbSequence;
        this.passThreshold    = passThreshold;
        this.peptides         = peptides;
        this.score            = score;
        this.threshold        = threshold;
        this.sequenceCoverage = sequenceCoverage;
        this.gel = gel;
    }

    public DBSequence getDbSequence() {
        return dbSequence;
    }

    public void setDbSequence(DBSequence dbSequence) {
        this.dbSequence = dbSequence;
    }

    public boolean isPassThreshold() {
        return passThreshold;
    }

    public void setPassThreshold(boolean passThreshold) {
        this.passThreshold = passThreshold;
    }

    public Map<PeptideEvidence, List<Peptide>> getPeptides() {
        return peptides;
    }

    public void setPeptides(Map<PeptideEvidence, List<Peptide>> peptides) {
        this.peptides = peptides;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getSequenceCoverage() {
        return sequenceCoverage;
    }

    public void setSequenceCoverage(double sequenceCoverage) {
        this.sequenceCoverage = sequenceCoverage;
    }

    public List<PeptideSequence> getPeptidesSequence() {
        ArrayList<PeptideSequence> result                = new ArrayList<PeptideSequence>();
        List<Peptide>              identifiedPeptideList = this.getIdentifiedPeptides();

        for (Peptide peptide : identifiedPeptideList) {
            result.add(peptide.getPeptideSequence());
        }

        return result;
    }

    public List<Peptide> getIdentifiedPeptides() {
        List<Peptide> identifiedPeptides = new ArrayList<Peptide>();

        for (PeptideEvidence key : peptides.keySet()) {
            identifiedPeptides.addAll(peptides.get(key));
        }

        return identifiedPeptides;
    }

    public Gel getGel() {
        return gel;
    }

    public void setGel(Gel gel) {
        this.gel = gel;
    }

    public IdentifiableParamGroup getProteinAmbiguityGroup() {
        return proteinAmbiguityGroup;
    }

    public void setProteinAmbiguityGroup(IdentifiableParamGroup proteinAmbiguityGroup) {
        this.proteinAmbiguityGroup = proteinAmbiguityGroup;
    }
}



