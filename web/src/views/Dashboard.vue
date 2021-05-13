<template>
	<v-container>
		<v-form>
			<v-text-field v-model="repo_url" label="GitLab Repo Url" required></v-text-field>
			<v-btn class="blue" @click="runLint()">Lint</v-btn>
		</v-form>

		<v-card class="mx-auto" max-width="344" outlined v-for="proj in projects" :key="proj.id">
			<v-list-item three-line>
				<v-list-item-content>
					<v-list-item-title class="headline mb-1">
						{{ proj.name }}
					</v-list-item-title>
				</v-list-item-content>
			</v-list-item>

			<v-card-actions>
				<v-btn outlined rounded text>
					<router-link :to="{ name: 'Result', params: { id: proj.id } }">Ergebnis</router-link>
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-container>
</template>

<script>
import ax from "@/api";

export default {
	name: "Dashboard",
	data() {
		return {
			projects: [],
			repo_url: "",
		};
	},
	mounted() {
		ax.get("/projects")
			.then((res) => {
				this.projects = res.data;
			})
			.catch((err) => {
				console.log(err);
			});
	},
	methods: {
		runLint() {
			ax.post("/projects", this.repo_url, {
				headers: { "Content-Type": "text/plain" },
			}).catch((err) => {
				console.log(err);
			});
		},
	},
};
</script>
