<template>
	<v-container>
		<v-form>
			<v-row>
				<v-col cols="10">
					<v-text-field v-model="repo_url" label="GitLab Repo Url" outlined dense filled required></v-text-field>
				</v-col>
				<v-col cols="2">
					<v-btn class="blue" large @click="runLint()">Lint</v-btn>
				</v-col>
			</v-row>
		</v-form>

		<v-tabs centered class="ml-n9" color="grey darken-1">
			<v-tab to="/projects">Projekte</v-tab>
			<v-tab to="/stats">Statistik</v-tab>
		</v-tabs>

		<router-view />
	</v-container>
</template>

<script>
import ax from "@/api";

export default {
	name: "Dashboard",
	data() {
		return {
			repo_url: "",
		};
	},
	methods: {
		runLint() {
			ax.post("/projects", this.repo_url, {
				headers: { "Content-Type": "text/plain" },
			}).catch((err) => {
				this.$em.emit("error", err);
			});
		},
	},
};
</script>
