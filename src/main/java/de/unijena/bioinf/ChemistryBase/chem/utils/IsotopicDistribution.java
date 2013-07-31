package de.unijena.bioinf.ChemistryBase.chem.utils;

import de.unijena.bioinf.ChemistryBase.algorithm.ParameterHelper;
import de.unijena.bioinf.ChemistryBase.algorithm.Parameterized;
import de.unijena.bioinf.ChemistryBase.chem.Element;
import de.unijena.bioinf.ChemistryBase.chem.Isotopes;
import de.unijena.bioinf.ChemistryBase.chem.PeriodicTable;
import de.unijena.bioinf.ChemistryBase.data.DataDocument;

import java.util.*;

public class IsotopicDistribution implements Parameterized {

	private final HashMap<String, Isotopes> isotopeMap;
    private ArrayList<Isotopes> isotopes;
    private PeriodicTable table;

    public IsotopicDistribution() {
        this(PeriodicTable.getInstance());
    }

    public IsotopicDistribution(PeriodicTable table) {
        this.table = table;
        this.isotopeMap = new HashMap<String, Isotopes>();
        this.isotopes = new ArrayList<Isotopes>();
    }

    public Isotopes getIsotopesFor(String symbol) {
        return isotopeMap.get(symbol);
    }

    public Isotopes getIsotopesFor(Element element) {
        if (this.isotopes.size() > element.getId()) {
            Isotopes iso = this.isotopes.get(element.getId());
            if (iso != null) return iso;
        }
        final Isotopes isotopes = isotopeMap.get(element.getSymbol());
        if (isotopes != null) {
            while (this.isotopes.size() <= element.getId()) this.isotopes.add(null);
            this.isotopes.set(element.getId(), isotopes);
            return isotopes;
        }
        return null;
    }

    public IsotopicDistribution subset(Iterable<Element> elements) {
        final IsotopicDistribution dist = new IsotopicDistribution(table);
        for (Element e : elements)
            dist.addIsotope(e.getSymbol(), getIsotopesFor(e));
        return dist;
    }

    public void merge(IsotopicDistribution otherDist){
        for (Map.Entry<String, Isotopes> entry : otherDist.isotopeMap.entrySet()) {
            addIsotope(entry.getKey(), entry.getValue());
        }
    }

    public void addIsotope(String elementSymbol, Isotopes isotopes) {
        final Element element = table.getByName(elementSymbol);
        isotopeMap.put(elementSymbol, isotopes);
        if (element != null) {
            while (this.isotopes.size() <= element.getId()) this.isotopes.add(null);
            this.isotopes.set(element.getId(), isotopes);
        }
    }

    public void addIsotope(String elementSymbol, double[] masses, double[] abundances) {
        addIsotope(elementSymbol, new Isotopes(masses, abundances));
    }

    @Override
    public <G, D, L> void importParameters(ParameterHelper helper, DataDocument<G, D, L> document, D dictionary) {
        final D isotopes = document.getDictionaryFromDictionary(dictionary, "isotopes");
        final Iterator<Map.Entry<String, G>> iter = document.iteratorOfDictionary(isotopes);
        while (iter.hasNext()) {
            final Map.Entry<String, G> entry = iter.next();
            addIsotope(entry.getKey(), new Isotopes().readFromParameters(helper, document, document.getDictionary(entry.getValue())));
        }
    }

    @Override
    public <G, D, L> void exportParameters(ParameterHelper helper, DataDocument<G, D, L> document, D dictionary) {
        final D dict = document.newDictionary();
        for (Map.Entry<String, Isotopes> e : isotopeMap.entrySet()) {
            final D iso = document.newDictionary();
            e.getValue().exportParameters(helper, document, iso);
            document.addDictionaryToDictionary(dict, e.getKey(), iso);
        }
        document.addDictionaryToDictionary(dictionary, "isotopes", dict);
    }
}
