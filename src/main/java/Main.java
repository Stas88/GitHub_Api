import net.GitHubApiPlaying;

/**
 * Created by stassikorskyi on 26.11.15.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        GitHubApiPlaying githubApi = new GitHubApiPlaying();
        //githubApi.runEmojis();
        //githubApi.runUserSearch("aka");
        githubApi.runRepoSearch("API", "java");
        //githubApi.runGetAllUsers();
        //githubApi.runGetReposCommits();
        //githubApi.runGetReposCommitsComments();
//        githubApi.runGetReposPages();
//        githubApi.runGetAllOrgs();
        //githubApi.runGetAllGists();
//        githubApi.runRepoReadme();
        //githubApi.runGetAllGistsWithPagination();
        //githubApi.runSearchWithPagination("API", "java", githubApi.SAVE_TO_PARSE_STORING_METHOD);
        //githubApi.getAuthorizationsGitHub();
        //githubApi.authorizeGitHub();
    }
}
