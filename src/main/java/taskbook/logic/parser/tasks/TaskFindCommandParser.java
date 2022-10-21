package taskbook.logic.parser.tasks;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import taskbook.commons.core.Messages;
import taskbook.logic.commands.tasks.TaskFindCommand;
import taskbook.logic.parser.ArgumentMultimap;
import taskbook.logic.parser.ArgumentTokenizer;
import taskbook.logic.parser.CliSyntax;
import taskbook.logic.parser.Parser;
import taskbook.logic.parser.Prefix;
import taskbook.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new TaskFindCommand
 */
public class TaskFindCommandParser implements Parser<TaskFindCommand> {
    // Note: the space at the start of the arguments is necessary due to ArgumentTokenizer behavior.
    private static final Pattern FIND =
            Pattern.compile(String.format("\\s+%s.*", CliSyntax.PREFIX_QUERY.getPrefix()));

    /**
     * Parses the given {@code String} of arguments in the context of the TaskFindCommand
     * and returns an TaskSortCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public TaskFindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_QUERY);

        if (!arePrefixesPresent(argMultimap, CliSyntax.PREFIX_QUERY)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(
                    Messages.MESSAGE_INVALID_COMMAND_FORMAT, TaskFindCommand.MESSAGE_USAGE));
        }
        String query = argMultimap.getValue(CliSyntax.PREFIX_QUERY)
                .orElseThrow(() -> new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                                                TaskFindCommand.MESSAGE_USAGE)));
        return new TaskFindCommand(query);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
