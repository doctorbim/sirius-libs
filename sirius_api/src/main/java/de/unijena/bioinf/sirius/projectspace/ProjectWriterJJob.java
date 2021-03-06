package de.unijena.bioinf.sirius.projectspace;

import de.unijena.bioinf.jjobs.BasicJJob;
import java.io.IOException;

public class ProjectWriterJJob extends BasicJJob{

    ProjectWriter writer;
    ExperimentResult experiment;

    public ProjectWriterJJob(ProjectWriter writer, ExperimentResult experiment) {
        super(JobType.IO);
        this.writer = writer;
        this.experiment = experiment;
    }

    @Override
    protected Object compute() throws IOException {
        writer.writeExperiment(experiment);
        return null;
    }

}
