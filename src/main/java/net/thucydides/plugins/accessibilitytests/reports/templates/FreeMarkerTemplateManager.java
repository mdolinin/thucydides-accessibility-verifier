package net.thucydides.plugins.accessibilitytests.reports.templates;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import net.thucydides.core.reports.templates.FreemarkerReportTemplate;

/**
 * Manages velocity templates.
 *
 */
public class FreeMarkerTemplateManager {

    Configuration cfg;

    public FreeMarkerTemplateManager() {
        cfg = new Configuration();
        cfg.setClassForTemplateLoading(getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }

    public FreemarkerReportTemplate getTemplateFrom(final String template) throws Exception {
        return new FreemarkerReportTemplate(cfg, template);
    }

}