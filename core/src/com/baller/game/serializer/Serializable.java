package com.baller.game.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class cannot exist without parent T class
 * */
public interface Serializable<T>{
      /**@return T object as serialized String
       * */
      String serialize();
      /**
       * @param serialized is the result of serialized method
       * if serialized String is not correct return null
       * */
      void deserialize(String serialized);
      boolean isEmpty();
      T construct();
}
