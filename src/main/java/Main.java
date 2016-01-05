import net.GitHubApiPlaying;

/**
 * Created by stassikorskyi on 26.11.15.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        GitHubApiPlaying githubApi = new GitHubApiPlaying();
        //githubApi.runEmojis();
        //githubApi.runUserSearch("aka");
        //githubApi.runRepoSearch("API", "java");
        //githubApi.runGetAllUsers();
        //githubApi.runGetAllOrgs();
        //githubApi.runGetAllGists();
      //  //githubApi.runGetAllGistsWithPagination();
        //githubApi.runGetAllReposWithPagination();
        //githubApi.runGetReposCommits();
        //githubApi.runGetReposCommitsComments();
//      githubApi.runGetReposPages();
        //githubApi.runRepoReadme();
        githubApi.runSearchWithPagination("java crawler", "java", githubApi.SAVE_TO_PARSE_STORING_METHOD);
        //githubApi.getAuthorizationsGitHub();
        //githubApi.authorizeGitHub();
    }
}
