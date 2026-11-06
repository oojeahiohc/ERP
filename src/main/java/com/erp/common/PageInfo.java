// 목록 페이지 정보와 조회 시작 위치 계산
package com.erp.common;

public record PageInfo(int page, int size, long totalCount, long totalPages) {
    // 페이지 번호 검증 및 전체 페이지 수 계산
    public static PageInfo of(int page, long totalCount) {
        if (page < 1)
            throw new IllegalArgumentException("page");
        return new PageInfo(page, 20, totalCount, Math.max(1, (totalCount + 19) / 20));
    }

    // 현재 페이지의 조회 시작 위치 계산
    public long getOffset() {
        return ((long) page - 1) * size;
    }

    // 현재 페이지 번호 조회
    public int getPage() {
        return page;
    }

    // 페이지당 조회 건수 조회
    public int getSize() {
        return size;
    }

    // 전체 조회 결과 건수 조회
    public long getTotalCount() {
        return totalCount;
    }

    // 전체 페이지 수 조회
    public long getTotalPages() {
        return totalPages;
    }
}
