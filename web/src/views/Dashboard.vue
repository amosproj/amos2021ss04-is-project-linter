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

		<!-- <v-app-bar>
			<v-tabs centered>
				<v-tab to="#">Projekte</v-tab>
				<v-tab to="#">Statistik</v-tab>
			</v-tabs>
		</v-app-bar> -->

		<v-row>
			<v-col v-for="proj in projects" :key="proj.id" cols="3">
				<v-card class="mx-auto" max-width="344" outlined height="250px">
					<v-card-title>{{ proj.name }}</v-card-title>
					<v-card-subtitle>
						<a :href="proj.url">{{ proj.url }}</a>
					</v-card-subtitle>
					<v-card-text>
						Forks: {{ proj.forkCount }}
					</v-card-text>
					<v-card-actions>
						<v-btn outlined rounded color="blue" text :to="{ name: 'Result', params: { id: proj.id } }">
							Ergebnis
						</v-btn>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>
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
				this.$em.emit("error", err);
			});
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
