package org.pitest.mutationtest.build.intercept.equivalent;

import org.junit.Test;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.intercept.javafeatures.FilterTester;
import org.pitest.mutationtest.engine.gregor.mutators.NullMutateEverything;
import org.pitest.mutationtest.engine.gregor.mutators.NullReturnValsMutator;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class NullFlatmapTest {

    MutationInterceptor testee = new NullFlatMapFilterFactory().createInterceptor(null);

    FilterTester verifier = new FilterTester("", this.testee, NullReturnValsMutator.NULL_RETURN_VALUES,
            new NullMutateEverything());

    @Test
    public void declaresTypeAsFilter() {
        assertThat(this.testee.type()).isEqualTo(InterceptorType.FILTER);
    }

    @Test
    public void filtersNullReturnMutantWhenMethodUsedOnlyInFlatMap() { //
        verifier.assertFiltersNMutationFromClass(1, HasPrivateStreamMethodUsedOnlyInSingleFlatMap.class);
    }

    @Test
    public void doesNotFilterNullReturnsInPublicStreamMethods() {
        verifier.assertFiltersNMutationFromClass(0, HasPublicStreamMethodUsedOnlyInSingleFlatMap.class);
    }

    @Test
    public void doesNotFilterOtherMutantsInNullReturnsStreamMethods() {
        verifier.assertFiltersNMutationFromClass(1, HasPrivateStreamMethodUsedOnlyInSingleFlatMapThatHasOtherMutableCode.class);
    }

    @Test
    public void doesNotFilterNullReturnsWhenOriginalNotEmptyStream() {
        verifier.assertFiltersNMutationFromClass(0, HasPrivateStreamMethodThatDoesNotReturnEmpty.class);
    }

    @Test
    public void doesNotFilterNullReturnsWhenNoFlatMapCallsExist() {
        verifier.assertFiltersNMutationFromClass(0, NotCalledFromFlatMap.class);
    }

    @Test
    public void doesNotFilterNullReturnsWhenNonFlatMapCallsExist() {
        verifier.assertFiltersNMutationFromClass(0, HasPrivateStreamMethodCalledNotFromFlatMap.class);
    }

}

class HasPrivateStreamMethodUsedOnlyInSingleFlatMap {
    public Stream<String> makesCall(List<String> l) {
        return l.stream()
                .flatMap(this::aStream);

    }

    private Stream<String> aStream(String l) {
        return Stream.empty();
    }
}

class HasPrivateStreamMethodUsedOnlyInSingleFlatMapThatHasOtherMutableCode {
    public Stream<String> makesCall(List<String> l) {
        return l.stream()
                .flatMap(this::aStream);

    }

    private Stream<String> aStream(String l) {
        System.out.println("Keep mutating me");
        return Stream.empty();
    }
}

class NotCalledFromFlatMap {
    public Stream<String> makesCall() {
        return aStream("")
                .map(s -> s + "boo");
    }

    private Stream<String> aStream(String l) {
        return Stream.empty();
    }
}

class HasPrivateStreamMethodCalledNotFromFlatMap {
    public Stream<String> makesCall(List<String> l) {
        return l.stream()
                .flatMap(this::aStream);

    }

    public Stream<String> alsoMakesCall() {
        return aStream("")
                .map(s -> s + "boo");
    }

    private Stream<String> aStream(String l) {
        return Stream.empty();
    }
}

class HasPrivateStreamMethodThatDoesNotReturnEmpty {
    public Stream<String> makesCall(List<String> l) {
        return l.stream()
                .flatMap(this::aStream);

    }

    private Stream<String> aStream(String l) {
        return Stream.of("");
    }
}


class HasPublicStreamMethodUsedOnlyInSingleFlatMap {
    public Stream<String> makesCall(List<String> l) {
        return l.stream()
                .flatMap(this::aStream);

    }

    public Stream<String> aStream(String l) {
        return Stream.empty();
    }
}