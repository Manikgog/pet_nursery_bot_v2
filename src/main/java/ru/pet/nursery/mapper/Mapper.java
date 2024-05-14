package ru.pet.nursery.mapper;

public interface Mapper<Input, Output> {
    Output perform(Input input);
}
