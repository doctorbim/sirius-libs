package de.unijena.bioinf.projectspace.sirius;

import de.unijena.bioinf.ChemistryBase.ms.Ms2Experiment;
import de.unijena.bioinf.babelms.ms.JenaMsParser;
import de.unijena.bioinf.babelms.ms.JenaMsWriter;
import de.unijena.bioinf.projectspace.ComponentSerializer;
import de.unijena.bioinf.projectspace.CompoundContainerId;
import de.unijena.bioinf.projectspace.ProjectReader;
import de.unijena.bioinf.projectspace.ProjectWriter;

import java.io.File;
import java.io.IOException;

public class MsExperimentSerializer implements ComponentSerializer<CompoundContainerId, CompoundContainer, Ms2Experiment> {

    @Override
    public Ms2Experiment read(ProjectReader reader, CompoundContainerId id, CompoundContainer container) throws IOException {
        return reader.textFile("spectrum.ms", (b)->new JenaMsParser().parse(b, new File(id.getDirectoryName(),"spectrum.ms").toURI().toURL()));
    }

    @Override
    public void write(ProjectWriter writer, CompoundContainerId id, CompoundContainer container, Ms2Experiment component) throws IOException {
        writer.textFile("spectrum.ms", (w)->new JenaMsWriter().write(w, component));
    }

    @Override
    public void delete(ProjectWriter writer, CompoundContainerId id, CompoundContainer container, Ms2Experiment component) throws IOException {
        writer.delete(id.getDirectoryName() + "/spectrum.ms");
    }
}
