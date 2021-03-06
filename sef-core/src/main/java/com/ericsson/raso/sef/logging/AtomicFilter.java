package com.ericsson.raso.sef.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

@Plugin(name = "AtomicFilter", category = "Core", elementType = "filter", printObject = true)
public class AtomicFilter extends AbstractFilter {

	private final Level exactLevel;
	
	protected AtomicFilter(Level level, Result onMatch, Result onMismatch) {
		super(onMatch, onMismatch);
		this.exactLevel = level;
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
		if (level.intLevel() != this.exactLevel.intLevel())
			return onMismatch;
		return onMatch;
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
		if (level.intLevel() != this.exactLevel.intLevel())
			return onMismatch;
		return onMatch;
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
		if (level.intLevel() != this.exactLevel.intLevel())
			return onMismatch;
		return onMatch;
	}

	@Override
	public Result filter(LogEvent event) {
		if (event.getLevel().intLevel() != this.exactLevel.intLevel())
			return onMismatch;
		return onMatch;
	}
	
	/**
     * Create a ThresholdFilter.
     * @param loggerLevel The log Level.
     * @param match The action to take on a match.
     * @param mismatch The action to take on a mismatch.
     * @return The created ThresholdFilter.
     */
    @PluginFactory
    public static AtomicFilter createFilter(@PluginAttribute("level") String level,
    											@PluginAttribute("onMatch") String match,
    											@PluginAttribute("onMismatch") String mismatch) {
        Level matchLevel = (level == null) ? Level.INFO : Level.toLevel(level.toUpperCase());
        Result onMatch = (match == null) ? Result.NEUTRAL : Result.valueOf(match.toUpperCase());
        Result onMismatch = mismatch == null ? Result.DENY : Result.valueOf(mismatch.toUpperCase());
 
        return new AtomicFilter(matchLevel, onMatch, onMismatch);
    }

	
	
}
