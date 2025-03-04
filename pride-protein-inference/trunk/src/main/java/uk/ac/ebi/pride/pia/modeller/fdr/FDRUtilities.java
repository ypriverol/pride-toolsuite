package uk.ac.ebi.pride.pia.modeller.fdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.pia.modeller.filter.psm.PSMAccessionsFilter;
import uk.ac.ebi.pride.pia.modeller.scores.ScoreUtilities;


/**
 * This class provides functionality for the FDR estimation using decoy proteins
 * 
 * @author julian
 *
 */
public class FDRUtilities {
	
	/**
	 * Calculate the FDR on the given score sorted List of
	 * {@link FDRComputableByDecoys} objects.
	 * 
	 * The item with the best score must be the first in the list, the worst
	 * score the last.
	 * 
	 * @param reportItems
	 */
	public static <T extends FDRComputableByDecoys> void calculateFDR(List<T> items, String scoreAccession) {
		double fdr;
		
		Double rankScore = Double.NaN;
		List<T> rankItems = new ArrayList<T>();
		
		int nrTargets = 0;
		int nrDecoys = 0;
		
		for (T item : items) {
			if (!rankScore.equals(item.getScore(scoreAccession))) {
				// this is a new rank, calculate FDR
				if (!rankScore.equals(Double.NaN) && (nrTargets < 1)) {
					// only decoys until now -> set FDR to infinity
					fdr = Double.POSITIVE_INFINITY;
				} else {
					fdr = (double)nrDecoys / nrTargets;
				}
				
				for (T rankItem : rankItems) {
					rankItem.setFDR(fdr);
				}
				
				rankScore = item.getScore(scoreAccession);
				rankItems.clear();
			}
			
			// check for decoy
			if (item.getIsDecoy()) {
				nrDecoys++;
			} else {
				nrTargets++;
			}
			
			rankItems.add(item);
		}
		
		// calculate the last rank
		if (nrTargets < 1) {
			// only decoys until now -> set FDR to infinity
			fdr = Double.POSITIVE_INFINITY;
		} else {
			fdr = (double)nrDecoys / nrTargets;
		}
		
		for (T rankItem : rankItems) {
			rankItem.setFDR(fdr);
		}
		
		
		// at last calculate the q-values
		// for this, iterate backwards through the list
		ListIterator<T> it  = items.listIterator(items.size());
		Double qValue = Double.NaN;
		
		while (it.hasPrevious()) {
			T item = it.previous();
			
			if ((qValue.compareTo(Double.NaN) == 0) ||
					(item.getFDR() < qValue)) {
				qValue = item.getFDR();
			}
			
			item.setQValue(qValue);
		}
	}
	
	
	/**
	 * Calculates the FDR score of the report. To do this, the report must have
	 * FDR values and be sorted.
	 * 
	 * @param items the list of items, for which the FDR will be calculated
	 * @param scoreAccession the accession of the score used for FDR calculation
	 * @param oboLookup whether ob olookup should be performed, if the score is not hard-coded
	 */
	public static <T extends FDRComputableByDecoys> void calculateFDRScore(
			List<T> items, String scoreAccession, boolean oboLookup) {
		if (items.size() < 2) {
			// no calculation for empty list possible
			return;
		}
		
		// get the stepPoints of the q-values
		List<Integer> stepPoints = new ArrayList<Integer>();
		ListIterator<T> it = items.listIterator(items.size());
		double qValue = Double.NaN;
		int nrDecoys = 0;
		int nrTargets = 0;
		
		while (it.hasPrevious()) {
			T item = it.previous();
			
			if ((Double.compare(qValue, Double.NaN) != 0) &&
					(item.getQValue() < qValue)) {
				stepPoints.add(it.nextIndex()+1);
			}
			
			qValue = item.getQValue();
			
			if (item.getIsDecoy()) {
				nrDecoys++;
			} else {
				nrTargets++;
			}
		}
		
		// calculate the FDR scores
		double g;
		double qLast, qNext;
		double sLast, sNext;
		
		Collections.sort(stepPoints);
		ListIterator<Integer> stepIterator = stepPoints.listIterator();
		Integer nextStep;
		
		if (ScoreUtilities.isHigherScoreBetter(scoreAccession, oboLookup)) {
			// get the score of the first entry + (difference between first entry and first decoy) / (index of first decoy)  (to avoid FDRScore = 0)
			if (stepPoints.size() > 0) {
				sLast = items.get(0).getScore(scoreAccession) +
						(items.get(0).getScore(scoreAccession) -
								items.get(stepPoints.get(0)).getScore(scoreAccession)) / stepPoints.get(0);
				
			} else {
				sLast = items.get(0).getScore(scoreAccession) + 
						(items.get(0).getScore(scoreAccession) -
								items.get(items.size()-1).getScore(scoreAccession)) / items.size()-1;
			}
		} else {
			// or 0, if not higherscorebetter
			sLast = 0;
		}
		qLast = 0;
		
		if (stepIterator.hasNext()) {
			nextStep = stepIterator.next();
			
			sNext = items.get(nextStep).getScore(scoreAccession);
			qNext = items.get(nextStep).getQValue();
		} else {
			// we add an artificial decoy to the end...
			nextStep = items.size();
			
			sNext = items.get(items.size()-1).getScore(scoreAccession);
			qNext = (nrTargets == 0) ? Double.POSITIVE_INFINITY : (double)(nrDecoys + 1) / nrTargets;
		}
		
		g = (qNext-qLast) / (sNext-sLast);
		
		it = items.listIterator();
		while (it.hasNext()) {
			T item = it.next();
			
			if (nextStep == it.nextIndex()-1) {
				if (stepIterator.hasNext()) {
					sLast = sNext;
					qLast = qNext;
					
					nextStep = stepIterator.next();
					
					sNext = items.get(nextStep).getScore(scoreAccession);
					qNext = items.get(nextStep).getQValue();
				}
				
				g = (qNext-qLast) / (sNext-sLast);
			}
			
			item.setFDRScore((item.getScore(scoreAccession)-sLast)*g + qLast);
		}
		
		
		System.err.println("decoys: " + nrDecoys + " targets: " + nrTargets);
	}
	
	
	/**
	 * This function marks the PSMs, which pass the given decoysFilter, as
	 * decoys for a subsequent FDR estimation.
	 * 
	 * @param psms 
	 * @param decoyFilter the filter which specifies the decoys
	 * @return the number of decoys in the list
	 */
	public static int markDecoys(List<IntermediatePeptideSpectrumMatch> psms,
			PSMAccessionsFilter decoysFilter) {
		int count = 0;
		for (IntermediatePeptideSpectrumMatch psm : psms) {
			if (decoysFilter.satisfiesFilter(psm)) {
				psm.setIsDecoy(true);
				count++;
			} else {
				psm.setIsDecoy(false);
			}
		}
		return count;
	}
}
