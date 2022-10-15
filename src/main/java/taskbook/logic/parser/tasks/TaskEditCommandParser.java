package taskbook.logic.parser.tasks;

import static java.util.Objects.requireNonNull;
import static taskbook.logic.parser.CliSyntax.PREFIX_ASSIGN_FROM;
import static taskbook.logic.parser.CliSyntax.PREFIX_ASSIGN_TO;
import static taskbook.logic.parser.CliSyntax.PREFIX_DATE;
import static taskbook.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static taskbook.logic.parser.CliSyntax.PREFIX_INDEX;

import taskbook.commons.core.Messages;
import taskbook.commons.core.index.Index;
import taskbook.logic.commands.tasks.TaskEditCommand;
import taskbook.logic.parser.ArgumentMultimap;
import taskbook.logic.parser.ArgumentTokenizer;
import taskbook.logic.parser.Parser;
import taskbook.logic.parser.ParserUtil;
import taskbook.logic.parser.exceptions.ParseException;
import taskbook.model.task.EditTaskDescriptor;
import taskbook.model.task.enums.Assignment;

/**
 * Parses input arguments and creates a new TaskEditCommand object
 */
public class TaskEditCommandParser implements Parser<TaskEditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public TaskEditCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
            args, PREFIX_INDEX, PREFIX_ASSIGN_TO, PREFIX_ASSIGN_FROM, PREFIX_DESCRIPTION, PREFIX_DATE);

        if (argMultimap.getValue(PREFIX_INDEX).isEmpty()) {
            throw new ParseException(
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, TaskEditCommand.MESSAGE_USAGE));
        }

        String stringIndex = argMultimap.getValue(PREFIX_INDEX).get();
        Index index;
        try {
            int integerIndex = Integer.parseInt(stringIndex);
            index = Index.fromOneBased(integerIndex);
        } catch (NumberFormatException ne) {
            throw new ParseException(
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, TaskEditCommand.MESSAGE_USAGE), ne);
        }

        EditTaskDescriptor editTaskDescriptor = new EditTaskDescriptor();

        if (argMultimap.getValue(PREFIX_ASSIGN_TO).isPresent()
            && argMultimap.getValue(PREFIX_ASSIGN_FROM).isPresent()) {
            throw new ParseException(TaskEditCommand.MESSAGE_ASSIGNOR_ASSIGNEE);
        }

        if (argMultimap.getValue(PREFIX_ASSIGN_TO).isPresent()) {
            editTaskDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_ASSIGN_TO).get()));
            editTaskDescriptor.setAssignment(Assignment.TO);
        }
        if (argMultimap.getValue(PREFIX_ASSIGN_FROM).isPresent()) {
            editTaskDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_ASSIGN_FROM).get()));
            editTaskDescriptor.setAssignment(Assignment.FROM);
        }
        if (argMultimap.getValue(PREFIX_DESCRIPTION).isPresent()) {
            editTaskDescriptor.setDescription(
                ParserUtil.parseDescription(argMultimap.getValue(PREFIX_DESCRIPTION).get()));
        }
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            editTaskDescriptor.setDate(ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get()));
        }

        if (!editTaskDescriptor.isAnyFieldEdited()) {
            throw new ParseException(TaskEditCommand.MESSAGE_NOT_EDITED);
        }

        return new TaskEditCommand(index, editTaskDescriptor);
    }
}