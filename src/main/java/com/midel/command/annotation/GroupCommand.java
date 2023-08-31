package com.midel.command.annotation;

import com.midel.command.Command;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark if {@link Command} can be accessed only by group.
 */
@Retention(RUNTIME)
public @interface GroupCommand {
}
