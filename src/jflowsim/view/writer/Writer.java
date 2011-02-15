package jflowsim.view.writer;

import jflowsim.model.numerics.UniformGrid;

public abstract class Writer {

    protected int scalar_type;
    protected boolean enabled = false;

    public Writer() {
        this.enabled = false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract String[] getScalars();

    public void setScalarType(int scalar_type) {
        this.scalar_type = scalar_type;
    }

    public abstract void write(UniformGrid grid);
}
