package uk.ac.ebi.pride.pia.modeller.filter.psm;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.pia.modeller.filter.AbstractFilter;
import uk.ac.ebi.pride.pia.modeller.filter.FilterComparator;
import uk.ac.ebi.pride.pia.modeller.filter.FilterType;

/**
 * Filters for accessions on PSM level.
 * 
 * @author julian
 *
 */
public class PSMAccessionsFilter extends AbstractFilter {
	
	protected static final String shortName = "psm_accessions_filter";
	
	private static final String name = "Accessions Filter for PSM";
	
	private static final String filteringName = "Accessions (PSM)";
	
	private static final FilterType filterType = FilterType.literal_list;
	
	private String value;
	
	
	public PSMAccessionsFilter(FilterComparator arg, String value, boolean negate) {
		this.comparator = arg;
		this.value = value;
		this.negate = negate;
	}
	
	
	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFilteringName() {
		return filteringName;
	}

	@Override
	public Object getFilterValue() {
		return value;
	}

	@Override
	public FilterType getFilterType() {
		return filterType;
	}

	@Override
	public Object getObjectsValue(Object o) {
		if (o instanceof IntermediatePeptideSpectrumMatch) {
			List<String> accessions = new ArrayList<String>();
			SpectrumIdentification psm = ((IntermediatePeptideSpectrumMatch) o).getSpectrumIdentification();
			for (PeptideEvidence pepEvidence : psm.getPeptideEvidenceList()) {
				accessions.add(pepEvidence.getDbSequence().getAccession());
			}
			return accessions;
		} else if (o instanceof SpectrumIdentification) {
			List<String> accessions = new ArrayList<String>();
			for (PeptideEvidence pepEvidence : ((SpectrumIdentification)o).getPeptideEvidenceList()) {
				accessions.add(pepEvidence.getDbSequence().getAccession());
			}
			return accessions;
		} else {
			return null;
		}
	}

	@Override
	public boolean supportsClass(Object c) {
		if ((c instanceof IntermediatePeptideSpectrumMatch) ||
				(c instanceof SpectrumIdentification)) {
			return true;
		} else {
			return false;
		}
	}

}
