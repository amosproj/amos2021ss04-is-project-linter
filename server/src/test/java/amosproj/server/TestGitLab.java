package amosproj.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class TestGitLab {

    @Autowired
    private GitLab api;

    @Test
    void test_makeApiRequest() throws JsonProcessingException {
        String data = "{\"id\":13555,\"description\":\"\",\"name\":\"Amos Testz\",\"name_with_namespace\":\"Lukas Böhm / Amos Testz\",\"path\":\"amos-testz\",\"path_with_namespace\":\"ib49uquh/amos-testz\",\"created_at\":\"2021-04-27T20:47:28.093Z\",\"default_branch\":\"master\",\"tag_list\":[],\"ssh_url_to_repo\":\"git@gitlab.cs.fau.de:ib49uquh/amos-testz.git\",\"http_url_to_repo\":\"https://gitlab.cs.fau.de/ib49uquh/amos-testz.git\",\"web_url\":\"https://gitlab.cs.fau.de/ib49uquh/amos-testz\",\"readme_url\":\"https://gitlab.cs.fau.de/ib49uquh/amos-testz/-/blob/master/README.md\",\"avatar_url\":null,\"forks_count\":1,\"star_count\":0,\"last_activity_at\":\"2021-05-25T19:56:56.787Z\",\"namespace\":{\"id\":2255,\"name\":\"Lukas Böhm\",\"path\":\"ib49uquh\",\"kind\":\"user\",\"full_path\":\"ib49uquh\",\"parent_id\":null,\"avatar_url\":\"/uploads/-/system/user/avatar/1864/avatar.png\",\"web_url\":\"https://gitlab.cs.fau.de/ib49uquh\"},\"_links\":{\"self\":\"https://gitlab.cs.fau.de/api/v4/projects/13555\",\"issues\":\"https://gitlab.cs.fau.de/api/v4/projects/13555/issues\",\"merge_requests\":\"https://gitlab.cs.fau.de/api/v4/projects/13555/merge_requests\",\"repo_branches\":\"https://gitlab.cs.fau.de/api/v4/projects/13555/repository/branches\",\"labels\":\"https://gitlab.cs.fau.de/api/v4/projects/13555/labels\",\"events\":\"https://gitlab.cs.fau.de/api/v4/projects/13555/events\",\"members\":\"https://gitlab.cs.fau.de/api/v4/projects/13555/members\"},\"packages_enabled\":true,\"empty_repo\":false,\"archived\":false,\"visibility\":\"public\",\"owner\":{\"id\":1864,\"name\":\"Lukas Böhm\",\"username\":\"ib49uquh\",\"state\":\"active\",\"avatar_url\":\"https://gitlab.cs.fau.de/uploads/-/system/user/avatar/1864/avatar.png\",\"web_url\":\"https://gitlab.cs.fau.de/ib49uquh\"},\"resolve_outdated_diff_discussions\":false,\"container_registry_enabled\":true,\"container_expiration_policy\":{\"cadence\":\"1d\",\"enabled\":false,\"keep_n\":10,\"older_than\":\"90d\",\"name_regex\":\".*\",\"name_regex_keep\":null,\"next_run_at\":\"2021-04-28T20:47:28.107Z\"},\"issues_enabled\":true,\"merge_requests_enabled\":true,\"wiki_enabled\":true,\"jobs_enabled\":true,\"snippets_enabled\":true,\"service_desk_enabled\":true,\"service_desk_address\":\"gitlab-issue+ib49uquh-amos-testz-13555-issue-@cip.cs.fau.de\",\"can_create_merge_request_in\":true,\"issues_access_level\":\"enabled\",\"repository_access_level\":\"enabled\",\"merge_requests_access_level\":\"enabled\",\"forking_access_level\":\"enabled\",\"wiki_access_level\":\"enabled\",\"builds_access_level\":\"enabled\",\"snippets_access_level\":\"enabled\",\"pages_access_level\":\"enabled\",\"operations_access_level\":\"enabled\",\"analytics_access_level\":\"enabled\",\"emails_disabled\":null,\"shared_runners_enabled\":true,\"lfs_enabled\":true,\"creator_id\":1864,\"import_status\":\"none\",\"open_issues_count\":0,\"ci_default_git_depth\":50,\"ci_forward_deployment_enabled\":true,\"public_jobs\":true,\"build_timeout\":3600,\"auto_cancel_pending_pipelines\":\"enabled\",\"build_coverage_regex\":null,\"ci_config_path\":null,\"shared_with_groups\":[],\"only_allow_merge_if_pipeline_succeeds\":false,\"allow_merge_on_skipped_pipeline\":null,\"restrict_user_defined_variables\":false,\"request_access_enabled\":true,\"only_allow_merge_if_all_discussions_are_resolved\":false,\"remove_source_branch_after_merge\":true,\"printing_merge_request_link_enabled\":true,\"merge_method\":\"merge\",\"suggestion_commit_message\":null,\"auto_devops_enabled\":false,\"auto_devops_deploy_strategy\":\"continuous\",\"autoclose_referenced_issues\":true,\"permissions\":{\"project_access\":null,\"group_access\":null}}";
        JsonNode expected = new ObjectMapper().readTree(data);
        JsonNode actual = api.makeApiRequest("/projects/13555");
        assertEquals(expected, actual);
    }

}
