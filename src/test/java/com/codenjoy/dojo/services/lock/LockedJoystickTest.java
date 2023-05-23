package com.codenjoy.dojo.services.lock;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.Joystick;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class LockedJoystickTest {

    private ReadWriteLock readWriteLock;
    private Joystick joystick;
    private LockedJoystick lockedJoystick;
    private Lock lock;

    @Before
    public void before() {
        // given
        readWriteLock = mock(ReadWriteLock.class);
        lock = mock(Lock.class);
        when(readWriteLock.writeLock()).thenReturn(lock);
        lockedJoystick = new LockedJoystick(readWriteLock);
        joystick = mock(Joystick.class);
        lockedJoystick.wrap(joystick);
    }

    @Test
    public void shouldLockWhenDown() {
        // when
        lockedJoystick.down();

        // then
        verifyWithLock(Joystick::down);
    }

    private void verifyWithLock(Consumer<Joystick> joystickAction) {
        InOrder inOrder = inOrder(lock, joystick);
        inOrder.verify(lock).lock();
        joystickAction.accept(inOrder.verify(joystick));
        inOrder.verify(lock).unlock();
    }

    @Test
    public void shouldLockWhenUp() {
        // when
        lockedJoystick.up();

        // then
        verifyWithLock(Joystick::up);
    }

    @Test
    public void shouldLockWhenLeft() {
        // when
        lockedJoystick.left();

        // then
        verifyWithLock(Joystick::left);
    }

    @Test
    public void shouldLockWhenRight() {
        // when
        lockedJoystick.right();

        // then
        verifyWithLock(Joystick::right);
    }

    @Test
    public void shouldLockWhenAct() {
        // given
        int[] parameters = new int[] {1, 2, 3};

        // when
        lockedJoystick.act(parameters);

        // then
        verifyWithLock(joystick -> joystick.act(parameters));
    }

    @Test
    public void shouldLockWhenMessage() {
        // given
        String message = "message";

        // when
        lockedJoystick.message(message);

        // then
        verifyWithLock(joystick -> joystick.message(message));
    }

    @Test
    public void shouldReturnWrappedJoystick() {
        // when
        Joystick wrappedJoystick = lockedJoystick.getWrapped();

        // then
        assertSame(joystick, wrappedJoystick);
    }
}