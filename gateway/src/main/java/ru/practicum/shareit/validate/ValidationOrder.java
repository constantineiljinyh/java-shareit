package ru.practicum.shareit.validate;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, Create.class, Update.class})
public interface ValidationOrder {}
