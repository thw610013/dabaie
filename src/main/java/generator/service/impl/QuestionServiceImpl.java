package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thw.dabaie.model.entity.Question;
import generator.service.QuestionService;
import com.thw.dabaie.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author tianhaowen
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2025-07-19 12:03:04
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




