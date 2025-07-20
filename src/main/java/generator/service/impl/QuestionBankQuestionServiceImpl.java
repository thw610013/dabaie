package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thw.dabaie.model.entity.QuestionBankQuestion;
import generator.service.QuestionBankQuestionService;
import com.thw.dabaie.mapper.QuestionBankQuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author tianhaowen
* @description 针对表【question_bank_question(题库题目)】的数据库操作Service实现
* @createDate 2025-07-19 12:03:04
*/
@Service
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
    implements QuestionBankQuestionService{

}




