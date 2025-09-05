package com.goormthon.univ.simhae.domain.dream.entity.value;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    DAILY_REFLECTION("일상 반영 꿈", "낮 동안의 경험이나 생각이 꿈속에 그대로 혹은 부분적으로 재현된 꿈"),
    WISH_FULFILLMENT("소망 충족 꿈", "현실에서 충족하지 못한 욕망이나 억압된 소망을 꿈속에서 달성하는 꿈"),
    NIGHTMARE("악몽 / 불안 꿈", "공포, 불안, 위협을 강하게 경험하게 되는 꿈"),
    RECURRING("반복 꿈", "동일하거나 유사한 내용이 여러 차례 반복되는 꿈"),
    SYMBOLIC("상징적 꿈", "직접적 사건이 아니라 상징적 이미지로 무의식을 드러내는 꿈"),
    PRECOGNITIVE("예지 / 직관 꿈", "미래 사건을 암시하거나 예감하는 꿈"),
    CREATIVE("창의적 / 문제 해결 꿈", "창작 활동이나 문제 해결 아이디어를 제공하는 꿈"),
    SOMATIC("신체 반영 꿈", "실제 신체 상태나 자극이 꿈에 반영되는 꿈"),
    LUCID("자각몽", "꿈꾸는 중에 자신이 꿈꾸고 있음을 자각하는 꿈"),
    SURREAL("초현실 / 비논리적 꿈", "현실의 법칙을 벗어난 기이하고 비논리적인 꿈");

    private final String name;        // 한글 이름
    private final String description; // 정의
}
