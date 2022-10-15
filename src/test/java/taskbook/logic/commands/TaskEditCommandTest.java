package taskbook.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static taskbook.commons.core.Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
import static taskbook.logic.commands.CommandTestUtil.TASK_STUDY;
import static taskbook.logic.commands.CommandTestUtil.TASK_WORK;
import static taskbook.logic.commands.CommandTestUtil.assertCommandFailure;
import static taskbook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static taskbook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import taskbook.commons.core.index.Index;
import taskbook.logic.commands.tasks.TaskEditCommand;
import taskbook.model.Model;
import taskbook.model.ModelManager;
import taskbook.model.UserPrefs;
import taskbook.model.person.Person;
import taskbook.model.task.EditTaskDescriptor;
import taskbook.model.task.Task;
import taskbook.model.task.Todo;
import taskbook.model.task.enums.Assignment;
import taskbook.testutil.EditTaskDescriptorBuilder;
import taskbook.testutil.PersonBuilder;
import taskbook.testutil.TaskBuilder;
import taskbook.testutil.TypicalIndexes;
import taskbook.testutil.TypicalTaskBook;

public class TaskEditCommandTest {

    @Test
    public void execute_validNameChange_success() {
        Model model = new ModelManager(TypicalTaskBook.getTypicalTaskBook(), new UserPrefs());
        Task task = TypicalTaskBook.EATING;
        Task editedTask = new TaskBuilder()
            .withPerson(TypicalTaskBook.BENSON)
            .withAssignment(Assignment.TO)
            .withDescription(task.getDescription().description)
            .withIsDone(task.isDone())
            .build();
        EditTaskDescriptor descriptor = new EditTaskDescriptorBuilder((Todo) editedTask).build();
        TaskEditCommand editCommand = new TaskEditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(TaskEditCommand.MESSAGE_EDIT_TASK_SUCCESS, editedTask);

        Model expectedModel = new ModelManager(TypicalTaskBook.getTypicalTaskBook(), new UserPrefs());
        expectedModel.setTask(model.getFilteredTaskList().get(0), editedTask);
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidContact_throwsCommandException() {
        Model model = new ModelManager(TypicalTaskBook.getTypicalTaskBook(), new UserPrefs());
        Task task = TypicalTaskBook.EATING;
        Person invalidPerson = new PersonBuilder().withName("notintaskbook").build();
        Task editedTask = new TaskBuilder()
            .withPerson(invalidPerson)
            .withAssignment(Assignment.TO)
            .withDescription(task.getDescription().description)
            .withIsDone(task.isDone())
            .build();
        EditTaskDescriptor descriptor = new EditTaskDescriptorBuilder((Todo) editedTask).build();
        TaskEditCommand editCommand = new TaskEditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = TaskEditCommand.MESSAGE_PERSON_NOT_FOUND;
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    @Test
    public void execute_indexOutOfBounds_throwsCommandException() {
        Model model = new ModelManager(TypicalTaskBook.getTypicalTaskBook(), new UserPrefs());
        Task task = TypicalTaskBook.EATING;
        Task editedTask = new TaskBuilder()
            .withPerson(TypicalTaskBook.BENSON)
            .withAssignment(Assignment.TO)
            .withDescription(task.getDescription().description)
            .withIsDone(task.isDone())
            .build();
        EditTaskDescriptor descriptor = new EditTaskDescriptorBuilder((Todo) editedTask).build();
        Index index = Index.fromZeroBased(model.getFilteredTaskList().size());
        TaskEditCommand editCommand = new TaskEditCommand(index, descriptor);

        assertCommandFailure(editCommand, model, MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final TaskEditCommand standardCommand = new TaskEditCommand(INDEX_FIRST_PERSON, TASK_WORK);

        // same values -> returns true
        EditTaskDescriptor copyDescriptor = new EditTaskDescriptor(TASK_WORK);
        TaskEditCommand commandWithSameValues = new TaskEditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertEquals(standardCommand, commandWithSameValues);

        // same object -> returns true
        assertEquals(standardCommand, standardCommand);

        // null -> returns false
        assertNotEquals(null, standardCommand);

        // different types -> returns false
        assertNotEquals(standardCommand, new ClearCommand());

        // different index -> returns false
        assertNotEquals(standardCommand, new TaskEditCommand(TypicalIndexes.INDEX_SECOND_PERSON, TASK_WORK));

        // different descriptor -> returns false
        assertNotEquals(standardCommand, new TaskEditCommand(INDEX_FIRST_PERSON, TASK_STUDY));
    }
}