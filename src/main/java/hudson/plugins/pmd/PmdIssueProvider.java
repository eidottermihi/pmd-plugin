package hudson.plugins.pmd;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.plugins.analysis.util.model.FileAnnotation;
import org.jenkinsci.plugins.codehealth.provider.Priority;
import org.jenkinsci.plugins.codehealth.provider.issues.AbstractIssueMapper;
import org.jenkinsci.plugins.codehealth.provider.issues.Issue;
import org.jenkinsci.plugins.codehealth.provider.issues.IssueProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Providing PMD issues for Codehealth Plugin.
 *
 * @author Michael Prankl
 */
@Extension
public class PmdIssueProvider extends IssueProvider {

    private final AbstractIssueMapper<FileAnnotation> issueMapper = new PmdIssueMapper();

    @Nonnull
    @Override
    public Collection<Issue> getIssues(AbstractBuild<?, ?> build) {
        PmdResult result = getResult(build);
        if (result != null) {
            return map(result.getAnnotations());
        }
        return Collections.emptyList();
    }

    private Collection<Issue> map(Set<FileAnnotation> annotations) {
        final List<Issue> issues = new ArrayList<Issue>(annotations.size());
        for (FileAnnotation annotation : annotations) {
            issues.add(issueMapper.map(annotation));
        }
        return issues;
    }

    private class PmdIssueMapper extends AbstractIssueMapper<FileAnnotation> {
        @Override
        public Issue map(FileAnnotation fileAnnotation) {
            return new Issue(fileAnnotation.getContextHashCode(), fileAnnotation.getMessage(),
                    Priority.valueOf(fileAnnotation.getPriority().name()));
        }
    }

    private PmdResult getResult(final AbstractBuild<?, ?> build) {
        for (Action action : build.getPersistentActions()) {
            if (action instanceof PmdResultAction) {
                return ((PmdResultAction) action).getResult();
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getOrigin() {
        return "pmd";
    }

    @Nonnull
    @Override
    public String getOriginPluginName() {
        return "PMD Plugin";
    }

    @Nullable
    @Override
    public String getProjectResultUrlName() {
        return "pmdResult";
    }

    @Nullable
    @Override
    public String getBuildResultUrlName() {
        return "pmd";
    }
}
