package uk.ac.ebi.pride.mzgraph;

import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.mol.PTModification;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.graph.SpectrumPanel;
import uk.ac.ebi.pride.mzgraph.gui.filter.AnnotationControlPanel;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * SpectrumBrowser visualize a spectrum with a control panel
 * <p/>
 * User: rwang
 * Date: 14-Aug-2010
 * Time: 11:18:28
 */
public class SpectrumBrowser extends MzGraphBrowser {
    private final SpectrumPanel spectrum;
    private final AnnotationControlPanel annotationControlPanel;

    public SpectrumBrowser() {
        spectrum = new SpectrumPanel();
        annotationControlPanel = new AnnotationControlPanel(spectrum);
        addComponents();
    }

    public SpectrumBrowser(double[] mz, double[] intensity) {
        spectrum = new SpectrumPanel(mz, intensity);
        annotationControlPanel = new AnnotationControlPanel(spectrum);
        addComponents();
    }

    private void addComponents() {
        // set spectrum as the main component
        sidePane.setMainComponent(spectrum);
        // add standard tool kits.
        addStandardToolKits();
        // add custom tool kits
        addCustomToolKits();
    }

    private void addCustomToolKits() {
        // mass difference
        Icon clearIcon = GUIUtilities.loadIcon("icon/16x16/clear.gif");
        sidePane.addCommand(clearIcon, null,
                ToolbarCommand.MASS_DIFF.getTooltip(), ToolbarCommand.MASS_DIFF.getActionCommand(), false);
        // peak list
        Icon peakIcon = GUIUtilities.loadIcon("icon/16x16/peak.png");
        sidePane.addCommand(peakIcon, null,
                ToolbarCommand.PEAK_LIST.getTooltip(), ToolbarCommand.PEAK_LIST.getActionCommand(), true);
        // annotation
        Icon controlIcon = GUIUtilities.loadIcon("icon/16x16/filter.png");
        sidePane.addComponent(controlIcon, null,
                ToolbarCommand.ANNOTATION.getTooltip(), ToolbarCommand.ANNOTATION.getActionCommand(), annotationControlPanel);
        Icon mzIcon = GUIUtilities.loadIcon("icon/16x16/mz.jpeg");
        sidePane.addCommand(mzIcon, null,
                ToolbarCommand.MZTABLE.getTooltip(), ToolbarCommand.MZTABLE.getActionCommand(), false);
    }

    public void displayMzTable(boolean display) {
        if (! display) {
            sidePane.removeActionCommand(ToolbarCommand.MZTABLE.getActionCommand());
        }
    }

    /**
     * Get spectrum panel
     *
     * @return SpectrumPanel   get spectrum panel
     */
    public SpectrumPanel getSpectrumPanel() {
        return spectrum;
    }

    /**
     * Get the source of the spectrum
     *
     * @return Source name
     */
    public String getSource() {
        return spectrum.getSource();
    }

    /**
     * Set the source of the spectrum
     *
     * @param source source name
     */
    public void setSource(String source) {
        spectrum.setSource(source);
    }

    /**
     * Get the id of the spectrum
     *
     * @return id  id of the spectrum
     */
    public Comparable getId() {
        return spectrum.getId();
    }

    public void setId(Comparable id) {
        spectrum.setId(id);
    }

    /**
     * Set the visibility of clear mass difference action.
     *
     * @param isEnabled true mean visible
     */
    public void enableClearMassDifferences(boolean isEnabled) {
        sidePane.enableAction(ToolbarCommand.MASS_DIFF.getActionCommand(), isEnabled);
    }

    /**
     * Invoke clear mass difference action, this is same as clicking on it.
     */
    public void clearMassDifferences() {
        sidePane.invokeAction(ToolbarCommand.MASS_DIFF.getActionCommand());
    }

    /**
     * Set the visibility of the peak list visibility action
     *
     * @param isEnabled true mean visible
     */
    public void enablePeakControl(boolean isEnabled) {
        sidePane.enableAction(ToolbarCommand.PEAK_LIST.getActionCommand(), isEnabled);
    }

    /**
     * Invoke peak control action, this is same as clicking on it.
     */
    public void invokePeakControl() {
        sidePane.invokeAction(ToolbarCommand.PEAK_LIST.getActionCommand());
    }

    /**
     * Set the visibility of the annotation control panel
     *
     * @param isEnabled true mean visible
     */
    public void enableAnnotationControl(boolean isEnabled) {
        sidePane.enableAction(ToolbarCommand.ANNOTATION.getActionCommand(), isEnabled);
    }

    /**
     * Invoke annotation control action, this is same as clicking on it.
     *
     * @param isVisible true means visible
     */
    public void setAnnotationControlVisible(boolean isVisible) {
        String actionCmd = ToolbarCommand.ANNOTATION.getActionCommand();
        if ((isVisible && !sidePane.isToggled(actionCmd)) || (!isVisible && sidePane.isToggled(actionCmd))) {
            sidePane.invokeAction(actionCmd);
        }
    }

    public void setPeptide(Peptide peptide) {
        spectrum.initMzTablePanel(peptide);
    }

    public void setPeaks(double[] mz, double[] intensity) {
        spectrum.setPeaks(mz, intensity);
    }

    public void addAllAnnotations(List<IonAnnotation> ions) {
        spectrum.addAllAnnotations(ions);
    }

    public void setShowAutoAnnotations(boolean showAuto) {
        spectrum.setShowAutoAnnotations(showAuto);
    }

    public void setShowManualAnnotations(boolean showManual) {
        spectrum.setShowManualAnnotations(showManual);
    }

    public void setAminoAcidAnnotationParameters(int peptideLength, Map<Integer, List<PTModification>> modifications) {
        spectrum.setAminoAcidAnnotationParameters(peptideLength, modifications);
    }
}
