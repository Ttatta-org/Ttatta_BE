package TtattaBackend.ttatta.service.OpenAiService;

import TtattaBackend.ttatta.domain.Diaries;

import java.util.List;

public class PromptBuilder {
    public static String buildPrompt(List<Diaries> diaries) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음은 사용자의 하루 동안의 일기입니다. 위치, 카테고리, 내용 정보를 바탕으로 오늘 하루를 요약해 주세요. 한 일기의 시작은 '오늘은' 으로 시작해 주세요. " +
                "또한 만약 하루에 일기가 2개 이상이라면, 각 일기 요약은 엔터로 구분해 주세요." +
                "'사용자는' 이런 표현은 쓰지 마세요. 중간중간 어울리는 이모티콘은 1~2개 넣어주세요 넣어주세요." +
                "말투는 ~했어요. 이런식으로 해주세요.\n");


        for (Diaries diary : diaries) {
            prompt.append("- 위치: ").append(diary.getLocationName()).append("\n");
            prompt.append("  카테고리: ").append(diary.getDiaryCategories()).append("\n");
            prompt.append("  내용: ").append(diary.getContent()).append("\n\n");
        }

        prompt.append("이 내용을 종합하여 3~4문장 정도로 자연스럽게 요약해 주세요.");

        return prompt.toString();
    }
}
