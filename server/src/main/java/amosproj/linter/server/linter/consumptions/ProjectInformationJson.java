package amosproj.linter.server.linter.consumptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectInformationJson {

    private long id;
    private String name;
    private String visibility;

    public ProjectInformationJson() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}

// example json answer from gitlab
//{
//    "id":26063188,
//    "description":"typer packaging for Kali Linux",
//    "name":"typer",
//    "name_with_namespace":"Kali Linux / Packages / typer",
//    "path":"typer",
//    "path_with_namespace":"kalilinux/packages/typer",
//    "created_at":"2021-04-21T15:12:31.268Z",
//    "default_branch":"kali/master",
//    "tag_list":[
//
//    ],
//    "ssh_url_to_repo":"git@gitlab.com:kalilinux/packages/typer.git",
//    "http_url_to_repo":"https://gitlab.com/kalilinux/packages/typer.git",
//    "web_url":"https://gitlab.com/kalilinux/packages/typer",
//    "readme_url":"https://gitlab.com/kalilinux/packages/typer/-/blob/kali/master/README.md",
//    "avatar_url":null,
//    "forks_count":0,
//    "star_count":0,
//    "last_activity_at":"2021-04-21T15:12:31.268Z",
//    "namespace":{
//    "id":5034987,
//    "name":"Packages",
//    "path":"packages",
//    "kind":"group",
//    "full_path":"kalilinux/packages",
//    "parent_id":5034914,
//    "avatar_url":"/uploads/-/system/group/avatar/5034987/package.png",
//    "web_url":"https://gitlab.com/groups/kalilinux/packages"
//    },
//    "container_registry_image_prefix":"registry.gitlab.com/kalilinux/packages/typer",
//    "_links":{
//    "self":"https://gitlab.com/api/v4/projects/26063188",
//    "issues":"https://gitlab.com/api/v4/projects/26063188/issues",
//    "merge_requests":"https://gitlab.com/api/v4/projects/26063188/merge_requests",
//    "repo_branches":"https://gitlab.com/api/v4/projects/26063188/repository/branches",
//    "labels":"https://gitlab.com/api/v4/projects/26063188/labels",
//    "events":"https://gitlab.com/api/v4/projects/26063188/events",
//    "members":"https://gitlab.com/api/v4/projects/26063188/members"
//    },
//    "packages_enabled":true,
//    "empty_repo":false,
//    "archived":false,
//    "visibility":"public",
//    "resolve_outdated_diff_discussions":false,
//    "container_registry_enabled":true,
//    "container_expiration_policy":{
//    "cadence":"1d",
//    "enabled":false,
//    "keep_n":10,
//    "older_than":"90d",
//    "name_regex":".*",
//    "name_regex_keep":null,
//    "next_run_at":"2021-04-22T15:12:31.291Z"
//    },
//    "issues_enabled":true,
//    "merge_requests_enabled":true,
//    "wiki_enabled":false,
//    "jobs_enabled":true,
//    "snippets_enabled":true,
//    "service_desk_enabled":true,
//    "service_desk_address":"incoming+kalilinux-packages-typer-26063188-issue-@incoming.gitlab.com",
//    "can_create_merge_request_in":true,
//    "issues_access_level":"enabled",
//    "repository_access_level":"enabled",
//    "merge_requests_access_level":"enabled",
//    "forking_access_level":"enabled",
//    "wiki_access_level":"disabled",
//    "builds_access_level":"enabled",
//    "snippets_access_level":"enabled",
//    "pages_access_level":"enabled",
//    "operations_access_level":"enabled",
//    "analytics_access_level":"enabled",
//    "emails_disabled":null,
//    "shared_runners_enabled":true,
//    "lfs_enabled":true,
//    "creator_id":3860137,
//    "import_status":"none",
//    "open_issues_count":0,
//    "ci_default_git_depth":50,
//    "ci_forward_deployment_enabled":true,
//    "public_jobs":true,
//    "build_timeout":3600,
//    "auto_cancel_pending_pipelines":"enabled",
//    "build_coverage_regex":null,
//    "ci_config_path":"debian/kali-ci.yml",
//    "shared_with_groups":[
//
//    ],
//    "only_allow_merge_if_pipeline_succeeds":false,
//    "allow_merge_on_skipped_pipeline":null,
//    "restrict_user_defined_variables":false,
//    "request_access_enabled":true,
//    "only_allow_merge_if_all_discussions_are_resolved":false,
//    "remove_source_branch_after_merge":true,
//    "printing_merge_request_link_enabled":true,
//    "merge_method":"merge",
//    "suggestion_commit_message":null,
//    "auto_devops_enabled":false,
//    "auto_devops_deploy_strategy":"continuous",
//    "autoclose_referenced_issues":true,
//    "approvals_before_merge":0,
//    "mirror":false,
//    "external_authorization_classification_label":"",
//    "marked_for_deletion_at":null,
//    "marked_for_deletion_on":null,
//    "requirements_enabled":true,
//    "security_and_compliance_enabled":false,
//    "compliance_frameworks":[
//
//    ],
//    "issues_template":null,
//    "merge_requests_template":null,
//    "permissions":{
//    "project_access":null,
//    "group_access":null
//    }
//    }