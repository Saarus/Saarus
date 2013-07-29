package org.saarus.knime;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Tuan Nguyen
 */
public class SaarusKnimePlugin extends Plugin {
    private static SaarusKnimePlugin plugin;
    
    public SaarusKnimePlugin() {
        super();
        plugin = this;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        plugin = null ;
    }

    /**
     * Returns the shared instance.
     * @return Singleton instance of the Plugin
     */
    public static SaarusKnimePlugin getDefault() { return plugin; }
}

