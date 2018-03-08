import org.gradle.api.Project
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import utils.GitUtils
import java.io.File

object GitSpec : Spek({
    describe("a git") {
        on("hash") {
            val hash = mock(Project::class.java).let { project ->
                `when`(project.rootDir).thenReturn(File("./"))
                GitUtils.gitHash(project)
            }?.lines()
            it("should return one line") {
                Assert.assertEquals(1, hash?.size)
            }
        }
    }
})