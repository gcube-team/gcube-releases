package org.gcube.searchsystem.workflow;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MatchReplacer {

    private final Pattern pattern;

    public MatchReplacer(Pattern pattern) {
        this.pattern = pattern;
    }

    public abstract String replacement(MatchResult matchResult);

    public String replace(String input) {

        Matcher m = pattern.matcher(input);

        StringBuffer sb = new StringBuffer();

        while (m.find())
            m.appendReplacement(sb, replacement(m.toMatchResult()));

        m.appendTail(sb);

        return sb.toString();
    }
}