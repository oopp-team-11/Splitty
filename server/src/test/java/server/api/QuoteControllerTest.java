/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Person;
import commons.Quote;

public class QuoteControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestQuoteRepository repo;

    private QuoteController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestQuoteRepository();
        sut = new QuoteController(random, repo);
    }

    @Test
    public void cannotAddNullPerson() {
        var actual = sut.add(getQuote(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void randomSelection() {
        sut.add(getQuote("q1"));
        sut.add(getQuote("q2"));
        nextInt = 1;
        var actual = sut.getRandom();

        assertTrue(random.wasCalled);
        assertEquals("q2", actual.getBody().quote);
    }

    @Test
    public void databaseIsUsed() {
        sut.add(getQuote("q1"));
        repo.calledMethods.contains("save");
    }

    private static Quote getQuote(String q) {
        return new Quote(new Person(q, q), q);
    }

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}