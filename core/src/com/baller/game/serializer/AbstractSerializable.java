package com.baller.game.serializer;

import java.beans.Transient;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

abstract public class AbstractSerializable<T> implements Serializable<T> {
private transient List<String> patterns;
private transient List<Consumer<String>> handlers;
private transient boolean isEmpty = true;

protected void setEmptyState(boolean emptyState) {
      isEmpty = emptyState;
}

/**
 * Patterns should has at least one group
 */
@Override
public void deserialize(String serialized) {
      List<Matcher> matchers = getPatterns().stream()
				   .map(pattern -> pattern.matcher(serialized))
				   .collect(Collectors.toList());
      List<Consumer<String>> handlers = getHandlers();
      boolean holdsGroups = true;
      for (Matcher matcher : matchers) {
	    if (!matcher.find()) {
		  holdsGroups = false;
		  break;
	    }
      }
      if (!holdsGroups) {
	    isEmpty = true;
	    return;
      }
      isEmpty = false;
      int index = 0;
      try {
	    for (Matcher matcher : matchers) {
		  handlers.get(index).accept(matcher.group(1));
		  index += 1;
	    }
      } catch (Exception e) {
	    e.printStackTrace();
	    isEmpty = true;
      }
}
@Override
public boolean isEmpty(){
      return isEmpty;
}
protected void addPatterns(List<String> patterns) {
      this.patterns = patterns;
}

protected List<Pattern> getPatterns() {
      return patterns.stream()
		 .map(Pattern::compile)
		 .collect(Collectors.toList());
}

protected List<Consumer<String>> getHandlers() {
      return this.handlers;
}

protected void addHandlers(List<Consumer<String>> handlers) {
      this.handlers = handlers;
}
}
