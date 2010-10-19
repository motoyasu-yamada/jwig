package com.spicysoft.jwig;

/**
 * Variables can by modified by filters.
 * Filters are separated from the variable by a pipe symbol (|) and
 * may have optional arguments in parentheses.
 * Multiple filters can be chained.
 * The output of one filter is applied to the next.
 *
 * {{ name|striptags|title }}
 *
 * for example will remove all HTML tags from the name and title-cases it.
 * Filters that accept arguments have parentheses around the arguments,
 * like a function call.
 *
 * This example will join a list by commas: {{ list|join(', ') }}.
 *
 * @SEE http://www.twig-project.org/book/02-Twig-for-Template-Designers
 */
public interface Filter
{
	Object invoke(final Environment environment,final Object piped,final Object[] params);
}
