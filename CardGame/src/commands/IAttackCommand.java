package commands;

import java.util.*;
import java.util.function.*;

import cards.*;
import players.*;

public interface IAttackCommand extends ICommand {
	public void SetEvilCardCommand(BiConsumer<IObservablePlayer, Boolean> doThingWithCard);
	public void SetEvilNumberCommand(BiConsumer<IObservablePlayer, Boolean> doThingANumberOfTimes);
}
