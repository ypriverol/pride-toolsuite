package uk.ac.ebi.pride.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.*;

/**
 * DataAccessUtilities provides methods for getting information out from the core objects.
 * <p/>
 * User: rwang, yperez
 * Date: 30-Aug-2010
 * Time: 11:52:45
 */
public final class DataAccessUtilities {

    public static final Logger logger = LoggerFactory.getLogger(DataAccessUtilities.class);

    /**
     * Private Constructor
     */
    private DataAccessUtilities() {
    }

    /**
     * Get a list of taxonomy accessions based on a given metadata
     *
     * @param metaData meta data
     * @return List<String>    a list of taxonomy ids
     */
    public static List<String> getTaxonomy(ExperimentMetaData metaData) {
        List<String> species = new ArrayList<String>();
        List<Sample> samples = metaData.getSamples();
        for (Sample sample : samples) {
            List<CvParam> cvParams = sample.getCvParams();
            for (CvParam cvParam : cvParams) {
                if (cvParam.getCvLookupID().equalsIgnoreCase("newt")) {
                    species.add(cvParam.getAccession());
                }
            }
        }
        return species;
    }

    /**
     * Get the project of a experiment
     *
     * @param metaData experiment metadata
     * @return String project name
     */
    public static String getProjectName(ExperimentMetaData metaData) {
        String project = null;

        List<CvParam> cvParams = metaData.getCvParams();
        for (CvParam cvParam : cvParams) {
            if (CvTermReference.PROJECT_NAME.getAccession().equals(cvParam.getAccession())) {
                project = cvParam.getValue();
            }
        }

        return project;
    }

    /**
     * Count the number of peaks of a spectrum
     *
     * @param spectrum spectrum
     * @return int number of peaks
     */
    public static int getNumberOfPeaks(Spectrum spectrum) {
        int numOfPeaks = -1;
        BinaryDataArray mzArr = spectrum.getBinaryDataArrays().get(0);
        if (mzArr != null) {
            numOfPeaks = mzArr.getDoubleArray().length;
        }
        return numOfPeaks;
    }

    /**
     * Get ms level of a spectrum
     *
     * @param spectrum spectrum
     * @return int ms level
     */
    public static int getMsLevel(Spectrum spectrum) {
        int msLevel = -1;
        List<Parameter> param = getParamByName(spectrum, "ms level");
        if (!param.isEmpty()) {
            String val = param.get(0).getValue();
            msLevel = Integer.parseInt(val);
        }
        return msLevel;
    }

    /**
     * Get precursor charge from param group
     *
     * @param paramGroup param group
     * @return precursor charge
     */
    public static Integer getPrecursorChargeParamGroup(ParamGroup paramGroup) {
        Integer charge = null;

        if (paramGroup != null) {
            Double c = getSelectedIonCvParamValue(paramGroup, CvTermReference.PSI_ION_SELECTION_CHARGE_STATE, CvTermReference.ION_SELECTION_CHARGE_STATE);
            if (c != null) {
                charge = c.intValue();
            }
        }

        return charge;
    }

    /**
     * Get value of selected ion cv param
     *
     * @param paramGroup param group
     * @param refs       a list of possible cv terms to search for
     * @return Double  value of the cv paramter
     */
    private static Double getSelectedIonCvParamValue(ParamGroup paramGroup, CvTermReference... refs) {
        Double value = null;
        // search PRIDE xml based charge
        for (CvTermReference ref : refs) {
            List<CvParam> cvParams = getCvParam(paramGroup, ref.getCvLabel(), ref.getAccession());
            if (cvParams != null && !cvParams.isEmpty()) {
                value = (cvParams.get(0).getValue() != null) ? new Double(cvParams.get(0).getValue()) : null;
            }
        }
        return value;
    }

    /**
     * Get precursor m/z value
     *
     * @param spectrum spectrum
     * @return double  precursor m/z
     */
    public static double getPrecursorMz(Spectrum spectrum) {
        double mz = -1;
        if (spectrum != null && spectrum.getPrecursors() != null && !spectrum.getPrecursors().isEmpty()) {
            List<Precursor> precursors = spectrum.getPrecursors();
            Double m = getSelectedIonMz(precursors.get(0), 0);
            if (m != null) {
                mz = m;
            }
        }
        return mz;
    }

    /**
     * Get precursor m/z value
     *
     * @param paramGroup param group
     * @return precursor m/z
     */
    public static double getPrecursorMz(ParamGroup paramGroup) {
        double mz = -1;

        if (paramGroup != null) {
            Double m = getSelectedCvParamValue(paramGroup, CvTermReference.PSI_ION_SELECTION_MZ, CvTermReference.ION_SELECTION_MZ);
            if (m != null) {
                mz = m;
            }
        }
        return mz;
    }

    /**
     * Get precursor intensity
     *
     * @param spectrum spectrum
     * @return double  precursor intensity
     */
    public static double getPrecursorIntensity(Spectrum spectrum) {
        double intent = -1;
        List<Precursor> precursors = spectrum.getPrecursors();
        if (!precursors.isEmpty()) {
            Double it = getSelectedIonIntensity(precursors.get(0), 0);
            if (it != null) {
                intent = it;
            }
        }
        return intent;
    }

    /**
     * Get the sum of all the peak intensities within a spectrum
     *
     * @param spectrum spectrum
     * @return double  sum of intensities
     */
    public static double getSumOfIntensity(Spectrum spectrum) {
        double sum = 0;
        BinaryDataArray intentArr = spectrum.getIntensityBinaryDataArray();
        if (intentArr != null) {
            double[] originalIntentArr = intentArr.getDoubleArray();
            for (double intent : originalIntentArr) {
                sum += intent;
            }
        }
        return sum;
    }

    /**
     * Get the ion charge for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion charge.
     */
    public static Double getSelectedIonCharge(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_CHARGE_STATE, CvTermReference.ION_SELECTION_CHARGE_STATE);
    }

    /**
     * Get the ion m/z value for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion m/z.
     */
    public static Double getSelectedIonMz(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_MZ, CvTermReference.ION_SELECTION_MZ);
    }


    /**
     * Get the ion intensity value for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion intensity.
     */
    public static Double getSelectedIonIntensity(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_INTENSITY, CvTermReference.ION_SELECTION_INTENSITY);
    }

    /**
     * Get value of selected ion cv param.
     *
     * @param precursor precursor
     * @param index     index of the selecte ion.
     * @param refs      a list of possible cv terms to search for.
     * @return Double   value of the cv parameter.
     */
    private static Double getSelectedIonCvParamValue(Precursor precursor, int index, CvTermReference... refs) {
        Double value = null;
        List<ParamGroup> selectedIons = precursor.getSelectedIons();
        if (index >= 0 && index < selectedIons.size()) {
            ParamGroup selectedIon = selectedIons.get(index);
            // search PRIDE xml based charge
            value = getSelectedCvParamValue(selectedIon, refs);
        }
        return value;
    }


    /**
     * Get value of selected ion cv param
     *
     * @param paramGroup param group
     * @param refs       a list of possible cv terms to search for
     * @return Double  value of the cv paramter
     */
    private static Double getSelectedCvParamValue(ParamGroup paramGroup, CvTermReference... refs) {
        Double value = null;
        // search PRIDE xml based charge
        for (CvTermReference ref : refs) {
            List<CvParam> cvParams = getCvParam(paramGroup, ref.getCvLabel(), ref.getAccession());
            if (!cvParams.isEmpty()) {
                value = new Double(cvParams.get(0).getValue());
            }
        }
        return value;
    }

    /**
     * This is convenient method for accessing peptide.
     *
     * @param ident identification object
     * @param index zero based index.
     * @return Peptide  peptide.
     */
    public static Peptide getPeptide(Protein ident, int index) {
        Peptide peptide = null;
        List<Peptide> peptides = ident.getPeptides();
        if (peptides != null && peptides.size() > index) {
            peptide = peptides.get(index);
        }
        return peptide;
    }

    /**
     * Convenient method for number of peptides
     *
     * @param ident identification object
     * @return int number of peptides
     */
    public static int getNumberOfPeptides(Protein ident) {
        List<Peptide> peptides = ident.getPeptides();
        return peptides == null ? 0 : peptides.size();
    }

    /**
     * Convenient method for getting the number of unique peptides.
     *
     * @param ident identification object
     * @return int  number of unique peptide sequences.
     */
    public static int getNumberOfUniquePeptides(Protein ident) {
        List<PeptideSequence> peptides = ident.getPeptidesSequence();
        int cnt = 0;
        List<String> seqs = new ArrayList<String>();
        for (PeptideSequence peptide : peptides) {
            String seq = peptide.getSequence();
            if (!seqs.contains(seq)) {
                seqs.add(seq);
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Check whether spectrum has fragment ion information
     *
     * @param spectrum spectrum
     * @return boolean true if spectrum contains fragment ion information.
     */
    public static boolean hasFragmentIon(Spectrum spectrum) {
        Peptide peptide = spectrum.getPeptide();
        return peptide != null && hasFragmentIon(peptide);
    }


    /**
     * Check whether peptide has fragment ion information
     *
     * @param peptide peptide
     * @return boolean true if peptide contains fragment ion information.
     */
    public static boolean hasFragmentIon(Peptide peptide) {
        List<FragmentIon> ions = peptide.getFragmentation();
        return !ions.isEmpty();
    }

    /**
     * Get the number of post translational modifications within a protein identification
     *
     * @param ident protein identification
     * @return int number of ptms
     */
    public static int getNumberOfPTMs(Protein ident) {
        int cnt = 0;
        List<Peptide> peptides = ident.getPeptides();
        for (Peptide peptide : peptides) {
            List<Modification> mods = peptide.getPeptideSequence().getModifications();
            if (mods != null) {
                cnt += mods.size();
            }
        }
        return cnt;
    }

    /**
     * Get the number of Substitution PTMs for an specific Protein Identification
     *
     * @param ident ID of the Protein Identification
     * @return Total number of Substitution PTMs
     */
    public static int getNumberOfSubstitutionPTMs(Protein ident) {
        int cnt = 0;
        List<Peptide> peptides = ident.getPeptides();
        for (Peptide peptide : peptides) {
            PeptideSequence peptideSequence = peptide.getPeptideSequence();
            if (peptideSequence != null) {
                List<SubstitutionModification> mods = peptideSequence.getSubstitutionModifications();
                cnt += mods.size();
            }
        }
        return cnt;
    }

    /**
     * Get the number of the modification of a peptide
     *
     * @param peptide Peptide Object
     * @return Total number of PTMs
     */
    public static int getNumberOfPTMs(Peptide peptide) {
        int cnt = 0;
        PeptideSequence peptideSequence = peptide.getPeptideSequence();
        if (peptideSequence != null) {
            List<Modification> mods = peptideSequence.getModifications();
            cnt = mods.size();
        }
        return cnt;
    }

    /**
     * Get the number of substitution modification of a peptide
     *
     * @param peptide Peptide
     * @return Total number of Substitution PTMs
     */
    public static int getNumberOfSubstitutionPTMs(Peptide peptide) {
        int cnt = 0;
        PeptideSequence peptideSequence = peptide.getPeptideSequence();
        if (peptideSequence != null) {
            List<SubstitutionModification> mods = peptideSequence.getSubstitutionModifications();
            cnt = mods.size();
        }
        return cnt;
    }

    /**
     * Get peptide score from a peptide object.
     *
     * @param paramGroup parameter group
     * @param seTypes    a list of search engine types
     * @return PeptideScore  peptide score
     */
    public static Score getPeptideScore(ParamGroup paramGroup, List<SearchEngineType> seTypes) {
        if (paramGroup == null || seTypes == null) {
            throw new IllegalArgumentException("Input arguments for getScore can not be null");
        }

        Score score = new Score();
        for (SearchEngineType type : seTypes) {
            List<CvTermReference> scoreCvTerms = type.getSearchEngineScores();
            for (CvTermReference scoreCvTerm : scoreCvTerms) {
                List<CvParam> scoreParams = getCvParam(paramGroup, scoreCvTerm.getCvLabel(), scoreCvTerm.getAccession());
                if (!scoreParams.isEmpty()) {
                    // Note: only take the first param as the valid score
                    CvParam scoreParam = scoreParams.get(0);
                    String numStr = scoreParam.getValue();
                    if (NumberUtilities.isNumber(numStr)) {
                        Double num = new Double(numStr);
                        score.addScore(type, scoreCvTerm, num);
                    } else {
                        score.addScore(type, scoreCvTerm, null);
                    }
                }
            }
        }

        return score;
    }

    /**
     * Search and find a list of search engine types from input parameter group.
     *
     * @param paramGroup parameter group
     * @return List<SearchEngineType>  a list of search engine
     */
    public static List<SearchEngineType> getSearchEngineTypes(ParamGroup paramGroup) {
        if (paramGroup == null) {
            throw new IllegalArgumentException("Input argument for getSearchEngineTypes can not be null");
        }

        List<SearchEngineType> types = new ArrayList<SearchEngineType>();
        for (SearchEngineType type : SearchEngineType.values()) {
            for (CvTermReference scoreCvTerm : type.getSearchEngineScores()) {
                if (!getCvParam(paramGroup, scoreCvTerm.getCvLabel(), scoreCvTerm.getAccession()).isEmpty()) {
                    types.add(type);
                    break;
                }
            }
        }

        return types;
    }


    public static Score getScore(ParamGroup params) {
        Score score = null;
        if (params != null) {
            List<SearchEngineType> searchEngineTypes = DataAccessUtilities.getSearchEngineTypes(params);
            score = new Score();
            for (SearchEngineType searchEngineType : searchEngineTypes) {
                for (CvParam term : params.getCvParams()) {
                    CvTermReference reference = CvTermReference.getCvRefByAccession(term.getAccession());
                    if (reference != null && NumberUtilities.isNumber(term.getValue())) {
                        score.addScore(searchEngineType, reference, new Double(term.getValue()));
                    }
                }
            }
        }
        return score;
    }


    /**
     * Get cv param by accession number and cv label.
     * This method tries to find the CvParam for the given accession and cvLabel.
     * IMPORTANT NOTE: As the cvLabel may not always be present, the method will
     * assume a valid match if the accession alone matches.
     * <p/>
     * ToDo: perhaps separate method without cvLabel would be better (then this one could fail if no cvLabel was found)
     *
     * @param paramGroup parameter group
     * @param cvLabel    cv label.
     * @param accession  cv accession.
     * @return CvParam  cv param.
     */
    public static List<CvParam> getCvParam(ParamGroup paramGroup, String cvLabel, String accession) {
        if (paramGroup == null || cvLabel == null || accession == null) {
            throw new IllegalArgumentException("Input arguments for getCvParam can not be null");
        }
        List<CvParam> cvParams = paramGroup.getCvParams();
        List<CvParam> cps = new ArrayList<CvParam>();
        for (CvParam param : cvParams) {
            if (param.getAccession().equalsIgnoreCase(accession)) {
//                if (param.getCvLookupID() != null && !param.getCvLookupID().equalsIgnoreCase(cvLabel)) {
//                    // this could be the wrong CV param!!
//                    logger.warn("We may have got the wrong CV param: " + param.toString() + " compare to cvLabel: [" + cvLabel + "] accession: [" + accession + "]");
//                    // ToDo: proper logging (should perhaps fail, see comment above)
//                }
                cps.add(param);
            }
        }
        return cps;
    }

    /**
     * Create a List of Cv Params
     *
     * @param value     Value of the CvParam
     * @param cvLabel   Label of the CvParam
     * @param accession Accession of the CvParam
     * @param name      Name
     * @return List<CvParam>
     */
    public static List<CvParam> getCvParam(String name, String cvLabel, String accession, String value) {
        List<CvParam> cvParams = new ArrayList<CvParam>();
        CvParam cvParam = new CvParam(accession, name, cvLabel, value, null, null, null);
        cvParams.add(cvParam);
        return cvParams;
    }

    /**
     * Get a list parameters using a given name.
     *
     * @param paramGroup parameter group
     * @param name       name string
     * @return List<Parameter> a list of parameters
     */
    public static List<Parameter> getParamByName(ParamGroup paramGroup, String name) {
        if (paramGroup == null || name == null) {
            throw new IllegalArgumentException("Input arguments for getParamByName can not be null");
        }
        List<Parameter> params = new ArrayList<Parameter>();

        List<CvParam> cvParams = paramGroup.getCvParams();
        for (CvParam cvParam : cvParams) {
            if (cvParam.getName().equalsIgnoreCase(name)) {
                params.add(cvParam);
            }
        }

        List<UserParam> userParams = paramGroup.getUserParams();
        for (UserParam userParam : userParams) {
            if (userParam.getName().equalsIgnoreCase(name)) {
                params.add(userParam);
            }
        }

        return params;
    }

    /**
     * Get peptide Evidence for a Lis of Peptide Identifications
     *
     * @param peptides List of Peptide Identifications
     * @return Map with Peptide Evidence an the List of Peptide Identifications related with this peptide Evidence
     */
    public static Map<PeptideEvidence, List<Peptide>> getPeptideEvidence(List<Peptide> peptides) {
        HashMap<PeptideEvidence, List<Peptide>> peptideEvidences = new HashMap<PeptideEvidence, List<Peptide>>();
        for (Peptide peptide : peptides) {
            for (PeptideEvidence peptideEvidence : peptide.getPeptideEvidenceList()) {
                if (peptideEvidences.containsKey(peptideEvidence)) {
                    List<Peptide> peptidesIn = peptideEvidences.get(peptideEvidence);
                    peptidesIn.add(peptide);
                    peptideEvidences.put(peptideEvidence, peptidesIn);
                } else {
                    List<Peptide> peptidesIn = new ArrayList<Peptide>();
                    peptidesIn.add(peptide);
                    peptideEvidences.put(peptideEvidence, peptidesIn);
                }
            }
        }
        return peptideEvidences;
    }

    public static Integer getPrecursorCharge(List<Precursor> precursors) {
        if (precursors != null) {
            for (Precursor precursor : precursors) {
                for (ParamGroup paramGroup : precursor.getSelectedIons()) {
                    Integer charge = getPrecursorChargeParamGroup(paramGroup);
                    if (charge != null) return charge;
                }
            }
        }

        return null;
    }

    /**
     * Number of aminoacids covered
     * @param protein
     * @return
     */
    public static Integer getProteinCoverage(Protein protein) {

        List<PeptideEvidence> evidences = new ArrayList<PeptideEvidence>();

        if (protein.getPeptides().size() > 0) {

            int numOfValidPeptides = 0;

            // remove invalid peptide
            String sequence = protein.getDbSequence().getSequence();

            if (sequence != null && !"".equals(sequence)) {
                Iterator<Peptide> peptideIter = protein.getPeptides().iterator();
                while (peptideIter.hasNext()) {
                    Peptide peptide = peptideIter.next();
                    if (DataAccessUtilities.isValidPeptideAnnotation(protein, peptide)) {
                        evidences.add(peptide.getPeptideEvidence());
                    } else {
                        numOfValidPeptides++;
                    }
                }
            }

            // peptide coverage array
            // it is the length of the protein sequence, and contains the count of sequence coverage for each position
            int length = sequence == null ? 0 : sequence.length();
            int[] coverageArr = new int[length];
            for (PeptideEvidence uniquePeptide : evidences) {
                Set<Integer> startingPos = new HashSet<Integer>();
                boolean strictValidPeptideAnnotation = DataAccessUtilities.isStrictValidPeptideAnnotation(protein, uniquePeptide);
                if (strictValidPeptideAnnotation) {
                    startingPos.add(uniquePeptide.getStartPosition() - 1);
                } else {
                    startingPos.addAll(DataAccessUtilities.searchStartingPosition(protein, uniquePeptide));
                }

                for (Integer start : startingPos) {
                    // if the position does match
                    int peptideLen = uniquePeptide.getPeptideSequence().length();
                    int end = start + peptideLen - 1;

                    // iterate peptide
                    for (int i = start; i <= end; i++) {
                        coverageArr[i] += 1;
                    }
                }
            }

            // colour code the peptide positions
            int numOfAminoAcidCovered = 0;
            for (int count : coverageArr) {
                if (count != 0) {
                    numOfAminoAcidCovered++;
                }
            }

            return numOfAminoAcidCovered;
        }

        return null;
    }

    private static Set<Integer> searchStartingPosition(Protein protein, PeptideEvidence uniquePeptide) {
        Set<Integer> pos = new HashSet<Integer>();

        if (protein.getDbSequence().getSequence() != null && uniquePeptide.getPeptideSequence().getSequence() != null) {
            String sequenceString = protein.getDbSequence().getSequence();
            String subSeq = uniquePeptide.getPeptideSequence().getSequence();
            int previousIndex = -1;
            int index = -1;

            while((index = (previousIndex == -1 ? sequenceString.indexOf(subSeq) : sequenceString.indexOf(subSeq, previousIndex + 1))) > -1) {
                pos.add(index);
                previousIndex = index;
            }
        }

        return pos;
    }

    private static boolean isStrictValidPeptideAnnotation(Protein protein, PeptideEvidence uniquePeptide) {
        if(uniquePeptide.getStartPosition() <= protein.getDbSequence().getLength() &&
                uniquePeptide.getStartPosition() >=1 &&
                uniquePeptide.getEndPosition() >= uniquePeptide.getStartPosition() &&
                protein.getDbSequence().getSequence().substring(uniquePeptide.getStartPosition() - 1, uniquePeptide.getEndPosition()) != null &&
                protein.getDbSequence().getSequence().substring(uniquePeptide.getStartPosition() - 1, uniquePeptide.getEndPosition()).equals(uniquePeptide.getPeptideSequence().getSequence().toUpperCase())){
            return true;
        }
        return false;

    }

    /**
     * This funtion validate if a peptide sequence is included inside a protein sequence
     * @param protein
     * @param peptide
     * @return
     */
    private static boolean isValidPeptideAnnotation(Protein protein, Peptide peptide) {
        return protein.getDbSequence().getSequence() != null
                && peptide.getPeptideSequence().getSequence() != null
                && protein.getDbSequence().getSequence().contains(peptide.getSequence());
    }


}
